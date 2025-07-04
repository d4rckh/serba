import { useState } from "react";
import { useLibraries, useCreateLibrary, useUpdateLibrary, useDeleteLibrary } from "./lib/queries";
import { Button } from "@/components/ui/button";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Edit, Trash, Plus } from "lucide-react";
import EditCreateDialog from "./EditCreateDialog";
import type { components } from "./lib/v1";

export default function LibrariesTab() {
  const { data: libraries, isLoading, isError } = useLibraries();
  const createLibrary = useCreateLibrary();
  const updateLibrary = useUpdateLibrary();
  const deleteLibrary = useDeleteLibrary();

  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingLibrary, setEditingLibrary] = useState<components["schemas"]["LibraryEntity"] | null>(null);

  const openCreateDialog = () => {
    setEditingLibrary(null);
    setDialogOpen(true);
  };

  const openEditDialog = (library: components["schemas"]["LibraryEntity"]) => {
    setEditingLibrary(library);
    setDialogOpen(true);
  };

  if (isLoading) return <div>Loading libraries...</div>;
  if (isError) return <div>Error loading libraries</div>;

  return (
    <div>
      <div className="flex justify-between mb-4">
        <h2 className="text-xl font-semibold">Libraries</h2>
        <Button onClick={openCreateDialog}><Plus size={16} /> Create Library</Button>
      </div>

      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Name</TableHead>
            <TableHead>Location</TableHead>
            <TableHead className="text-right">Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {libraries?.map(lib => (
            <TableRow key={lib.id}>
              <TableCell>{lib.name}</TableCell>
              <TableCell>{lib.systemLocation}</TableCell>
              <TableCell className="text-right space-x-2">
                <Button
                  size="icon"
                  variant="ghost"
                  onClick={() => openEditDialog(lib)}
                  aria-label="Edit library"
                >
                  <Edit className="w-4 h-4" />
                </Button>
                <Button
                  size="icon"
                  variant="ghost"
                  onClick={() => {
                    if (confirm(`Delete library "${lib.name}"?`)) {
                      deleteLibrary.mutate({
                        body: lib
                      });
                    }
                  }}
                  aria-label="Delete library"
                >
                  <Trash className="w-4 h-4 text-red-600" />
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      <EditCreateDialog
        isOpen={dialogOpen}
        onClose={() => setDialogOpen(false)}
        initialData={editingLibrary}
        onSave={(data: components["schemas"]["LibraryEntity"]) => {
          if (editingLibrary) {
            updateLibrary.mutate({body: data}, { onSuccess: () => setDialogOpen(false) });
          } else {
            createLibrary.mutate({body: data}, { onSuccess: () => setDialogOpen(false) });
          }
        }}
      />
    </div>
  );
}
