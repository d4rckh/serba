import React, { useState } from "react";
import { Button } from "@/components/ui/button";
import { Folder, FileText, ArrowLeft, ArrowRight, DownloadIcon } from "lucide-react";
import {
  Table,
  TableBody,
  TableCell,
  TableRow,
} from "@/components/ui/table";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { getZipDownloadUrl, useCreateZipJobMutation, useLibraryFiles, useZipJobs } from "./lib/queries";
import type { components } from "./lib/v1";
import {
  Dialog,
  DialogFooter,
  DialogTrigger,
  DialogContent,
  DialogTitle,
  DialogClose,
  DialogDescription,
} from "./components/ui/dialog";
import {
  ContextMenu,
  ContextMenuTrigger,
  ContextMenuContent,
  ContextMenuItem,
} from "@/components/ui/context-menu";
import { Card, CardDescription, CardFooter, CardHeader, CardTitle } from "./components/ui/card";

interface FileBrowserProps {
  libraries: components["schemas"]["LibraryEntity"][];
}

export function FileDialog({
  libraryId,
  item,
  children,
  path,
}: {
  path: string;
  libraryId: number;
  children: React.ReactNode;
  item: components["schemas"]["SystemFileFolder"];
}) {
  const getDownloadUrl = () => {
    const fullPath = [path, item.name].join("/").replace(/\/+/g, "/");
    const encoded = encodeURIComponent(fullPath);
    return `${import.meta.env.PROD ? "/" : "/api/"
      }libraries/${libraryId}/download?path=${encoded}`;
  };

  return (
    <Dialog>
      <DialogTrigger asChild>{children}</DialogTrigger>
      <DialogContent>
        <DialogTitle>{item.name}</DialogTitle>
        <DialogDescription>
          {item.size ? `Size: ${(item.size / 1024).toFixed(2)} KB` : "Folder"}
        </DialogDescription>
        <DialogFooter>
          <DialogClose asChild>
            <Button asChild>
              <a
                href={`${getDownloadUrl()}`}
                target="_blank"
                rel="noopener noreferrer"
              >
                Download
              </a>
            </Button>
          </DialogClose>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

function FileItem({
  item,
  path,
  libraryId,
  onEnterFolder,
}: {
  item: components["schemas"]["SystemFileFolder"];
  path: string;
  libraryId: number;
  onEnterFolder: (folder: string) => void;
}) {
  const { mutateAsync: createZip } = useCreateZipJobMutation();

  const handleZipDownload = () => {
    const fullPath = [path, item.name].join("/").replace(/\/+/g, "/");
    createZip(
      {
        body: { libraryId, libraryPath: fullPath }
      }
    )
  };

  const getDirectDownloadUrl = () => {
    const fullPath = [path, item.name].join("/").replace(/\/+/g, "/");
    const encoded = encodeURIComponent(fullPath);
    return `${import.meta.env.PROD ? "/" : "/api/"}libraries/${libraryId}/download?path=${encoded}`;
  };

  const content = (
    <div className="flex items-center gap-2">
      {item.type === "FOLDER" ? (
        <Folder className="w-4 h-4 text-blue-500" />
      ) : (
        <FileText className="w-4 h-4 text-gray-500" />
      )}
      <span>{item.name}</span>
    </div>
  );

  return (
    <ContextMenu>
      <ContextMenuTrigger asChild>
        <TableRow
          key={item.path}
          className="cursor-pointer hover:bg-muted/50"
          onClick={() => {
            if (item.type === "FOLDER") onEnterFolder(item.name as string);
          }}
        >
          {item.type === "FILE" ? (
            <FileDialog path={path} libraryId={libraryId} item={item}>
              <TableCell>
                {content}
              </TableCell>
            </FileDialog>
          ) : (
            <TableCell>
              {content}
            </TableCell>
          )}
        </TableRow>
      </ContextMenuTrigger>

      <ContextMenuContent className="w-60">
        {item.type === "FOLDER" ? (
          <>
            <ContextMenuItem onSelect={() => onEnterFolder(item.name as string)}>
              <ArrowRight className="mr-2 h-4 w-4" /> Open
            </ContextMenuItem>

            <ContextMenuItem onSelect={handleZipDownload}>
              <DownloadIcon className="mr-2 h-4 w-4" />
              Start zip job
            </ContextMenuItem>
          </>
        ) : (
          <ContextMenuItem
            onSelect={() => window.open(getDirectDownloadUrl(), "_blank")}
          >
            <DownloadIcon className="mr-2 h-4 w-4" /> Download
          </ContextMenuItem>
        )}
      </ContextMenuContent>
    </ContextMenu>
  );
}

export default function FileBrowser({ libraries }: FileBrowserProps) {
  const [selectedLibraryId, setSelectedLibraryId] = useState(
    libraries.length > 0 ? libraries[0].id : 0
  );
  const [path, setPath] = useState("/");

  const zipJobs = useZipJobs();

  const onSelectLibrary = (id: number) => {
    setSelectedLibraryId(id);
    setPath("/");
  };

  const { data, isLoading, isError } = useLibraryFiles(
    selectedLibraryId ?? 0,
    path
  );

  const parts = path.split("/").filter(Boolean);

  const goBack = () => {
    const newPath = "/" + parts.slice(0, -1).join("/");
    setPath(newPath || "/");
  };

  const enterFolder = (folder: string) => {
    const newPath = path.endsWith("/") ? path + folder : path + "/" + folder;
    setPath(newPath);
  };

  if (libraries.length === 0) {
    return <div className="text-muted-foreground">No libraries available.</div>;
  }

  return (
    <div className="space-y-4">
      {
        zipJobs.data && zipJobs.data.map(a => <>
          <Card>
            <CardHeader>
              <CardTitle>{a.attrs?.libraryPath as unknown as string} zip</CardTitle>
              <CardDescription>Progress: {a.progress}%</CardDescription>
            </CardHeader>
            <CardFooter>
              <a
                href={`${getZipDownloadUrl(a.id as string)}`}
                target="_blank"
                rel="noopener noreferrer"
              >
                <Button disabled={(a.progress ?? 0) < 100}>Download now</Button>
              </a>

            </CardFooter>
          </Card>
        </>)
      }

      {/* Tabs for libraries */}
      <Tabs
        value={String(selectedLibraryId)}
        onValueChange={(val) => onSelectLibrary(Number(val))}
      >
        <TabsList className="flex gap-2">
          {libraries.map((lib) => (
            <TabsTrigger key={lib.id} value={String(lib.id)}>
              {lib.name}
            </TabsTrigger>
          ))}
        </TabsList>
      </Tabs>

      {/* Path navigation */}
      <div className="flex justify-between items-center">
        <div className="text-sm text-muted-foreground">
          Path:{" "}
          <span className="font-medium text-foreground">
            {path
              .split("/")
              .filter(Boolean)
              .reduce((acc, segment) => {
                const prevPath = acc.length > 0 ? acc[acc.length - 1][0] : "";
                const newPath = `${prevPath}/${segment}`;
                acc.push([newPath, segment] as [string, string]);
                return acc;
              }, [] as [string, string][])
              .map(([fullPath, name]) => (
                <Button
                  key={fullPath}
                  size="sm"
                  variant="ghost"
                  onClick={() => setPath(fullPath)}
                >
                  <Folder /> {name}
                </Button>
              ))}
          </span>
        </div>
        {path !== "/" && (
          <Button size="sm" onClick={goBack}>
            <ArrowLeft className="w-4 h-4 mr-1" />
            Back
          </Button>
        )}
      </div>


      {/* File list */}
      {isLoading && <div className="text-sm">Loading...</div>}
      {isError && (
        <div className="text-sm text-red-500">Failed to load files.</div>
      )}

      {!isLoading && !isError && (
        <Table>
          <TableBody>
            {data?.map((item) => (
              <FileItem
                key={item.path}
                item={item}
                path={path}
                libraryId={selectedLibraryId as number}
                onEnterFolder={enterFolder}
              />
            ))}
          </TableBody>
        </Table>
      )}
    </div>
  );
}
