import React, { useEffect, useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import type { components } from "./lib/v1";

interface EditCreateDialogProps {
  isOpen: boolean;
  onClose: () => void;
  initialData?: components["schemas"]["LibraryEntity"] | null;
  onSave: (data: components["schemas"]["LibraryEntity"]) => void;
}

export default function EditCreateDialog({ isOpen, onClose, initialData, onSave }: EditCreateDialogProps) {
  const [name, setName] = useState("");
  const [systemLocation, setSystemLocation] = useState("");

  useEffect(() => {
    if (initialData) {
      setName(initialData.name ?? "");
      setSystemLocation(initialData.systemLocation ?? "");
    } else {
      setName("");
      setSystemLocation("");
    }
  }, [initialData]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSave({ id: initialData?.id, name, systemLocation });
  };

  return (
    <Dialog open={isOpen} onOpenChange={open => !open && onClose()}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{initialData ? "Edit Library" : "Create Library"}</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <Label htmlFor="name">Name</Label>
            <Input
              id="name"
              value={name}
              onChange={e => setName(e.target.value)}
              required
              autoFocus
            />
          </div>

          <div>
            <Label htmlFor="location">System Location</Label>
            <Input
              id="location"
              value={systemLocation}
              onChange={e => setSystemLocation(e.target.value)}
              required
            />
          </div>

          <DialogFooter>
            <Button type="submit">{initialData ? "Save" : "Create"}</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
