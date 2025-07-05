import { useState } from "react";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import LibrariesTab from "./LibrariesTab";
import DownloadsTab from "./DownloadsTab";
import UsersTab from "./UsersTab";

export default function AdminDashboard() {
  const [tab, setTab] = useState("libraries");

  return (
    <div className="p-6 max-w-7xl mx-auto">
      <Tabs value={tab} onValueChange={setTab}>
        <TabsList>
          <TabsTrigger value="libraries">Libraries</TabsTrigger>
          <TabsTrigger value="downloads">Downloads</TabsTrigger>
          <TabsTrigger value="users">Users</TabsTrigger>
        </TabsList>

        <TabsContent value="libraries">
          <LibrariesTab />
        </TabsContent>

        <TabsContent value="downloads">
          <DownloadsTab />
        </TabsContent>

        <TabsContent value="users">
          <UsersTab />
        </TabsContent>
      </Tabs>
    </div>
  );
}
