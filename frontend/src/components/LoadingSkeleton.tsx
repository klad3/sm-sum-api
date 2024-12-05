import React from 'react';
import CircularProgress from '@mui/material/CircularProgress';

export function LoadingSkeleton() {
    return (
        <div className="flex flex-col justify-center items-center h-screen">
            <CircularProgress/>
            <p className="mt-4 text-gray-600">Cargando...</p>
        </div>
    );
}
