import { useDownloadTracking } from "./lib/queries";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

export default function DownloadsTab() {
  const { data: downloads, isLoading, isError } = useDownloadTracking();

  if (isLoading) return <div>Loading downloads...</div>;
  if (isError) return <div>Error loading downloads</div>;

  return (
    <div>
      <h2 className="text-xl font-semibold mb-4">Recent Downloads</h2>
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>User</TableHead>
            <TableHead>Library</TableHead>
            <TableHead>Path</TableHead>
            <TableHead>Bytes Read / Total</TableHead>
            <TableHead>Started At</TableHead>
            <TableHead>Completed At</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {downloads?.map((dl) => (
            <TableRow key={`${dl.user.id}-${dl.startedAt}`}>
              <TableCell>{dl.user.username}</TableCell>
              <TableCell>{dl.library.name}</TableCell>
              <TableCell>{dl.path}</TableCell>
              <TableCell>{`${dl.bytesRead} / ${dl.totalBytes}`}</TableCell>
              <TableCell>{new Date(dl.startedAt).toLocaleString()}</TableCell>
              <TableCell>{dl.completedAt ? new Date(dl.completedAt).toLocaleString() : "-"}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
}
