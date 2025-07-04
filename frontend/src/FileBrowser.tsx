import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Folder, FileText, ArrowLeft } from "lucide-react";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { useLibraryFiles } from "./lib/queries";
import type { components } from "./lib/v1";

interface FileBrowserProps {
  libraries: components["schemas"]["LibraryEntity"][];
}

export default function FileBrowser({ libraries }: FileBrowserProps) {
  const [selectedLibraryId, setSelectedLibraryId] = useState(
    libraries.length > 0 ? libraries[0].id : 0
  );
  const [path, setPath] = useState("/");

  const onSelectLibrary = (id: number) => {
    setSelectedLibraryId(id);
    setPath("/");
  };

  const { data, isLoading, isError } = useLibraryFiles(selectedLibraryId ?? 0, path);

  const parts = path.split("/").filter(Boolean);

  const goBack = () => {
    const newPath = "/" + parts.slice(0, -1).join("/");
    setPath(newPath || "/");
  };

  const enterFolder = (folder: string) => {
    const newPath = path.endsWith("/") ? path + folder : path + "/" + folder;
    setPath(newPath);
  };

  const getDownloadUrl = (relativePath: string) => {
    const encoded = encodeURIComponent(relativePath);
    return `http://localhost:8080/libraries/${selectedLibraryId}/download?path=${encoded}`;
  };

  return (
    <div className="space-y-4">
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
          Path: <span className="font-medium text-foreground">{path}</span>
        </div>
        {path !== "/" && (
          <Button size="sm" variant="outline" onClick={goBack}>
            <ArrowLeft className="w-4 h-4 mr-1" />
            Back
          </Button>
        )}
      </div>

      {/* File list */}
      {isLoading && <div className="text-sm">Loading...</div>}
      {isError && <div className="text-sm text-red-500">Failed to load files.</div>}

      {!isLoading && !isError && (
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Item</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {data?.map((item) => {
              const fullPath = [path, item.name].join("/").replace(/\/+/g, "/");

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
                <TableRow
                  key={item.path}
                  className="cursor-pointer hover:bg-muted/50"
                >
                  <TableCell
                    onClick={() => {
                      if (item.type === "FOLDER") enterFolder(item.name as string);
                    }}
                  >
                    {item.type === "FILE" ? (
                      <a
                        href={getDownloadUrl(fullPath)}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="block w-full"
                      >
                        {content}
                      </a>
                    ) : (
                      content
                    )}
                  </TableCell>
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      )}
    </div>
  );
}
