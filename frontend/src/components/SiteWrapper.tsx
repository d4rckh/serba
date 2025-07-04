import { useEffect } from "react";
import { useUserQuery } from "../lib/queries";
import { useLocation, useNavigate } from "react-router";

export default function SiteWrapper({ children }: { children: React.ReactNode }) {
  const user = useUserQuery();
  let navigate = useNavigate();
  let location = useLocation();
  useEffect(() => {
    if (user.isError) {
      if (location.pathname !== "/login") {
        navigate("/login");
      }
    } else if (user.isSuccess && user.data) {
      if (location.pathname === "/login") {
        navigate("/");
      }
      console.log("User data fetched successfully:", user.data);
    }
  }, [user]);


  return (
    <div className="max-w-4xl mx-auto">
      {children}
    </div>
  );
}