import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router";
import App from "./App";
import SiteWrapper from "./components/SiteWrapper";
import { QueryClientProvider } from "@tanstack/react-query";
import { queryClient } from "./lib/api";
import Login from "./Login";
import "./index.css"
import AdminDashboard from "./AdminDashboard";
import { ThemeProvider } from "./components/theme-provider";

const root = document.getElementById("root");

ReactDOM.createRoot(root!).render(
  <BrowserRouter>
    <QueryClientProvider client={queryClient}>
      <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
        <SiteWrapper>
          <Routes>
            <Route path="/" element={<App />} />
            <Route path="/login" element={<Login />} />
            <Route path="/admin" element={<AdminDashboard />} />
          </Routes>
        </SiteWrapper>
      </ThemeProvider>
    </QueryClientProvider>
  </BrowserRouter>
);
