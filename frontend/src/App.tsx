import { useLogout, useUserQuery, useLibraries } from "./lib/queries";
import FileBrowser from "./FileBrowser";
import { Button } from "@/components/ui/button";

function App() {
  const user = useUserQuery();
  const logout = useLogout();
  const libraries = useLibraries();

  if (user.isLoading || libraries.isLoading) return <div>Loading...</div>;
  if (user.isError || libraries.isError) return <div>Error loading data.</div>;
  if (!libraries.data) return <>Loading libraries</>;
  
  return (
    <div className="p-4">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-xl font-bold">Welcome, {user.data?.username}</h1>
        <Button onClick={() => logout()}>Logout</Button>
      </div>

      <FileBrowser libraries={libraries.data} />

    </div>
  );
}

export default App;
