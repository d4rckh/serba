"use client";

import { useState } from "react";
import {
  useUsers,
  useCreateUser,
  useDeleteUser,
  useUpdatePassword,
  useUserDownloadTracking,
  useUserPermissions,
  useUpdateUserLibraryAccessMutation,
} from "@/lib/queries";

import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import type { components } from "./lib/v1";
import { Card, CardContent, CardFooter } from "./components/ui/card";
import { ChevronDown, ChevronUp } from "lucide-react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "./components/ui/dialog";
import { Checkbox } from "./components/ui/checkbox";
import { DialogDescription } from "@radix-ui/react-dialog";

interface Props {
  user: components["schemas"]["UserEntity"];
  onDelete: () => void;
}

export function PermissionEditor({ perm }: { perm: components["schemas"]["UserLibraryAccessEntity"] }) {
  const editMutation = useUpdateUserLibraryAccessMutation();

  return <>
    <p className="flex items-center"> <Checkbox
      className="mr-2"
      checked={perm.viewLibrary}
      onCheckedChange={(checked) => editMutation.mutate({
        body: { ...perm, viewLibrary: checked === true },
      })}
    /> Can view '{perm.library?.name}'</p>

  </>
}

export function UserDialog({ user }: { user: components["schemas"]["UserEntity"] }) {
  const permissions = useUserPermissions(user.id as number);

  return <>
    <Dialog>
      <DialogTrigger asChild>
        <Button variant="outline">
          View Permissions
        </Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Permissions for {user.username}</DialogTitle>
          <DialogDescription>Note: you can't change the permissions for superusers.</DialogDescription>
        </DialogHeader>
        {
          permissions.data?.map((perm) => <PermissionEditor key={perm.id} perm={perm} />)
        }
      </DialogContent>
    </Dialog>
  </>
}

export function UserCard({ user, onDelete }: Props) {
  const [expanded, setExpanded] = useState(false);
  const { data: downloads, isLoading } = useUserDownloadTracking(user.id as number);

  return (
    <Card>
      <CardContent className="p-4 space-y-2">
        <div className="flex justify-between items-center">
          <div>
            <p className="font-medium">{user.username}</p>
            <p className="text-sm text-muted-foreground">
              Created at: {user.createdAt}
            </p>
          </div>
          <div className="flex gap-2">
            <Button
              size="icon"
              variant="ghost"
              onClick={() => setExpanded(prev => !prev)}
            >
              {expanded ? <ChevronUp /> : <ChevronDown />}
            </Button>
          </div>
        </div>

        {expanded && (
          <div className="pt-2 space-y-2">
            <h4 className="font-semibold text-sm">Downloads:</h4>
            {isLoading && <p className="text-sm text-muted-foreground">Loading...</p>}
            {!isLoading && downloads?.length === 0 && (
              <p className="text-sm text-muted-foreground">No downloads</p>
            )}
            {!isLoading && downloads && downloads.length > 0 && (
              <div className="space-y-1">
                {downloads.map((d, i) => (
                  <div
                    key={i}
                    className="border border-muted rounded p-2 bg-muted/30 text-sm"
                  >
                    <p><b>Library:</b> {d.library.name}</p>
                    <p><b>Path:</b> {d.path}</p>
                    <p><b>Progress:</b> {((d.bytesRead / d.totalBytes) * 100).toFixed(1)}%</p>
                    <p><b>Started:</b> {new Date(d.startedAt).toLocaleString()}</p>
                    {d.completedAt && (
                      <p><b>Completed:</b> {new Date(d.completedAt).toLocaleString()}</p>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </CardContent>
      <CardFooter className="flex gap-2">
        <Button variant="destructive" onClick={onDelete}>
          Delete
        </Button>
        <UserDialog user={user} />
      </CardFooter>
    </Card>
  );
}

export default function UsersTab() {
  const { data: users, isLoading } = useUsers();
  const createUser = useCreateUser();
  const updatePassword = useUpdatePassword();
  const deleteUser = useDeleteUser();

  const [newUser, setNewUser] = useState({ username: "", password: "" });
  const [passwordUpdate, setPasswordUpdate] = useState({ username: "", password: "" });

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-bold">All Users</h2>

      {isLoading && <p>Loading...</p>}
      {!isLoading && users && (
        <div className="grid md:grid-cols-2 gap-4">
          {users.map((user) => (
            <UserCard
              key={user.id}
              user={user}
              onDelete={() => deleteUser.mutate({ body: user })}
            />
          ))}
        </div>
      )}

      {/* Create User */}
      <div className="space-y-4">
        <h3 className="text-lg font-semibold">Create User</h3>
        <div className="flex flex-col gap-2 md:flex-row md:items-end">
          <div className="flex flex-col">
            <Label>Username</Label>
            <Input
              value={newUser.username}
              onChange={(e) => setNewUser({ ...newUser, username: e.target.value })}
            />
          </div>
          <div className="flex flex-col">
            <Label>Password</Label>
            <Input
              type="password"
              value={newUser.password}
              onChange={(e) => setNewUser({ ...newUser, password: e.target.value })}
            />
          </div>
          <Button onClick={() => createUser.mutate({ body: newUser })}>
            Create
          </Button>
        </div>
      </div>

      {/* Change Password */}
      <div className="space-y-4">
        <h3 className="text-lg font-semibold">Change Password</h3>
        <div className="flex flex-col gap-2 md:flex-row md:items-end">
          <div className="flex flex-col">
            <Label>Username</Label>
            <Input
              value={passwordUpdate.username}
              onChange={(e) =>
                setPasswordUpdate({ ...passwordUpdate, username: e.target.value })
              }
            />
          </div>
          <div className="flex flex-col">
            <Label>New Password</Label>
            <Input
              type="password"
              value={passwordUpdate.password}
              onChange={(e) =>
                setPasswordUpdate({ ...passwordUpdate, password: e.target.value })
              }
            />
          </div>
          <Button onClick={() => updatePassword.mutate({ body: passwordUpdate })}>
            Update
          </Button>
        </div>
      </div>
    </div>
  );
}
