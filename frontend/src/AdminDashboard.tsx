import { useState } from "react";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import LibrariesTab from "./LibrariesTab";
import DownloadsTab from "./DownloadsTab";

export default function AdminDashboard() {
  const [tab, setTab] = useState("libraries");

  return (
    <div className="p-6 max-w-7xl mx-auto">
      <Tabs value={tab} onValueChange={setTab}>
        <TabsList>
          <TabsTrigger value="libraries">Libraries</TabsTrigger>
          <TabsTrigger value="downloads">Downloads</TabsTrigger>
        </TabsList>

        <TabsContent value="libraries">
          <LibrariesTab />
        </TabsContent>

        <TabsContent value="downloads">
          <DownloadsTab />
        </TabsContent>
      </Tabs>
    </div>
  );
}
