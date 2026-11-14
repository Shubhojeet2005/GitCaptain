"use client";

import { useRouter } from "next/navigation";
import { createElement, useEffect } from "react";

import { useCurrentUser } from "@/hooks/use-auth";
import { Spinner } from "@/components/ui/spinner";

export default function AuthCallbackPage() {
  const router = useRouter();
  const { data: user, isLoading, isError, isFetched } = useCurrentUser();

  useEffect(() => {
    if (!isFetched || isLoading) return;

    if (user) {
      router.replace("/dashboard");
      return;
    }

    router.replace("/login?error=session");
  }, [user, isLoading, isFetched, isError, router]);

  return createElement(
    "div",
    { className: "flex min-h-svh flex-col items-center justify-center gap-3" },
    createElement(Spinner, { className: "size-6" }),
    createElement(
      "p",
      { className: "text-sm text-muted-foreground" },
      "Finishing GitHub sign-in…",
    ),
  );
}
