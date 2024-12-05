'use client'
import { useEffect, useState } from 'react';
import { CreateUserForm} from "@/components/LoginForm";
import { LoadingSkeleton } from "@/components/LoadingSkeleton";

export default function LoginPage() {
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const handlePageLoad = () => {
            setIsLoading(false);
        };

        if (document.readyState === "complete") {
            handlePageLoad();
        } else {
            window.addEventListener("load", handlePageLoad);
        }

        return () => window.removeEventListener("load", handlePageLoad);
    }, []);

    if (isLoading) {
        return <LoadingSkeleton />;
    }

    return <CreateUserForm />;
}