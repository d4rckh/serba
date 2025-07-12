import { useDeleteDownloadTrackingMutation, useDownloadTracking } from "./lib/queries";
import { Progress } from "@/components/ui/progress";
import { Badge } from "@/components/ui/badge";
import { Button } from "./components/ui/button";
import { Trash } from "lucide-react";

export default function DownloadsTab() {
  const { data: downloads, isLoading, isError } = useDownloadTracking();
  const deleteMutation = useDeleteDownloadTrackingMutation();

  if (isLoading) return <div className="text-muted-foreground">Loading downloads...</div>;
  if (isError) return <div className="text-destructive">Error loading downloads</div>;

  const pending = downloads?.filter(dl => !dl.completedAt) ?? [];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-semibold">Recent Downloads</h2>
        <Badge variant={pending.length > 0 ? "secondary" : "outline"}>
          {pending.length} Pending
        </Badge>
      </div>

      <div className="space-y-4">
        {downloads?.map((dl) => {
          const progress = dl.totalBytes > 0
            ? Math.round((dl.bytesRead / dl.totalBytes) * 100)
            : 0;

          return (
            <div
              key={`${dl.user.id}-${dl.startedAt}`}
              className="border rounded-lg p-4 bg-muted/30 space-y-2"
            >
              <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-1">
                <div>
                  <Button disabled={(progress ?? 0) < 100} variant={"ghost"} onClick={() => {
                    deleteMutation.mutate({
                      params: {
                        path: { downloadUuid: dl.uuid }
                      }
                    })
                  }}>
                    <Trash />
                  </Button>
                </div>
                <div className="flex flex-col">
                  <span className="text-sm font-medium">{dl.user.username}</span>
                  <span className="text-sm text-muted-foreground">{dl.library.name}</span>
                </div>
                <div className="flex-1"></div>
                <div className="text-sm text-muted-foreground max-w-[80ch] truncate">
                  {dl.path}
                </div>
              </div>

              <div className="flex flex-col gap-1">
                <Progress value={progress} />
                <div className="text-xs text-muted-foreground">
                  {`${dl.bytesRead} / ${dl.totalBytes} bytes (${progress}%) • avg: ${dl.averageSpeed} B/s`}
                </div>
              </div>

              <div className="flex flex-col sm:flex-row sm:justify-between text-sm text-muted-foreground gap-1">
                <span>Started: {new Date(dl.startedAt).toLocaleString()}</span>
                <span>
                  {dl.completedAt
                    ? `Completed: ${new Date(dl.completedAt).toLocaleString()}`
                    : <Badge variant="outline">Pending</Badge>}
                </span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
