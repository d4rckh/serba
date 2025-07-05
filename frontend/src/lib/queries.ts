import { useQueryClient } from "@tanstack/react-query";
import { $api } from "./api";

export default function useLogin() {
  const queryClient = useQueryClient();

  return (username: string, password: string) => {
    if (!username || !password) {
      return;
    }

    return fetch(import.meta.env.PROD ? "/login" : "/api/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      credentials: "include",
      body: JSON.stringify({
        username, password
      })
    }).then(() => {
      queryClient.refetchQueries($api.queryOptions("get", "/users/me"))
    })
  };
}

export function useLogout() {
  const queryClient = useQueryClient();
  return () => {
    fetch(import.meta.env.PROD ? "/signout" : "/api/signout", {
      method: "POST",
      credentials: "include",
    }).then(() => {
      queryClient.refetchQueries($api.queryOptions("get", "/users/me"))
    })
  };
}

export function useUserQuery() {
  return $api.useQuery("get", "/users/me", undefined, {
    retry: false,
  });
}


//
// LIBRARIES
//
export function useLibraries() {
  const user = useUserQuery();
  return $api.useQuery('get', '/libraries', undefined, {
    enabled: user.isSuccess,
    retry: false,
  });
}

export function useLibraryById(id: number) {
  const user = useUserQuery();
  return $api.useQuery(
    'get',
    '/libraries/{id}',
    { params: { path: { id } } },
    { enabled: !!id && user.isSuccess }
  );
}

export function useLibraryFiles(id: number, path = '/') {
  const user = useUserQuery();
  return $api.useQuery(
    'get',
    '/libraries/{id}/files',
    { params: { path: { id }, query: { path } } },
    { enabled: !!id && user.isSuccess }
  );
}

export function useCreateLibrary() {
  const queryClient = useQueryClient();
  return $api.useMutation('post', '/libraries', {
    onSettled: () => {
      queryClient.refetchQueries($api.queryOptions('get', '/libraries'));
    },
  });
}

export function useUpdateLibrary() {
  const queryClient = useQueryClient();
  return $api.useMutation('put', '/libraries', {
    onSettled: () => {
      queryClient.refetchQueries($api.queryOptions('get', '/libraries'));
    },
  });
}

//
// DOWNLOAD TRACKING
//
export function useDownloadTracking() {
  const user = useUserQuery();
  return $api.useQuery('get', '/tracking', undefined, {
    enabled: user.isSuccess,
    refetchInterval: 5000,
  });
}

export function useUserDownloadTracking(userId: number) {
  const user = useUserQuery();
  return $api.useQuery(
    'get',
    '/tracking/user/{userId}',
    { params: { path: { userId } } },
    { enabled: !!userId && user.isSuccess }
  );
}

export function useLibraryDownloadTracking(libraryId: number) {
  const user = useUserQuery();
  return $api.useQuery(
    'get',
    '/tracking/library/{libraryId}',
    { params: { path: { libraryId } } },
    { enabled: !!libraryId && user.isSuccess, refetchInterval: 5000 }
  );
}

export function useDeleteLibrary() {
  const queryClient = useQueryClient();
  return $api.useMutation("delete", "/libraries", {
    onSettled: () => {
      queryClient.refetchQueries($api.queryOptions("get", "/libraries"));
    },
  });
}

export function useUsers() {
  const user = useUserQuery();
  return $api.useQuery("get", "/users", undefined, {
    enabled: user.isSuccess,
    retry: false,
  });
}

export function useCreateUser() {
  const queryClient = useQueryClient();
  return $api.useMutation("post", "/users", {
    onSettled: () => {
      queryClient.refetchQueries($api.queryOptions("get", "/users"));
    },
  });
}

export function useUpdatePassword() {
  const queryClient = useQueryClient();
  return $api.useMutation("put", "/users/password", {
    onSettled: () => {
      queryClient.refetchQueries($api.queryOptions("get", "/users"));
    },
  });
}

export function useDeleteUser() {
  const queryClient = useQueryClient();
  return $api.useMutation("delete", "/users", {
    onSettled: () => {
      queryClient.refetchQueries($api.queryOptions("get", "/users"));
    },
  });
}