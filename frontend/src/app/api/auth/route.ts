// src/app/api/auth/route.ts
import { NextResponse } from 'next/server';

const apiBaseUrl: string = process.env.API_BASE_URL!;


export async function POST(req: Request) {
    try {
        const { user, password }: { user: string; password: string } = await req.json();

        if (!user || !password) {
            return NextResponse.json(
                { message: 'Faltan parámetros requeridos (user, password)' },
                { status: 400 }
            );
        }

        // Obtener apiKey y apiSecret de las cabeceras
        const apiKey = req.headers.get('x-api-key-id');
        const apiSecret = req.headers.get('x-api-secret');

        console.log('API Key:', apiKey);
        console.log('API Secret:', apiSecret);

        if (!apiKey || !apiSecret) {
            return NextResponse.json(
                { message: 'API Key y Secret requeridos en las cabeceras' },
                { status: 400 }
            );
        }

        // Realizamos la solicitud al backend para verificar las credenciales
        const loginResponse = await fetch(`${apiBaseUrl}/user/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'x-api-key-id': apiKey,
                'x-api-secret': apiSecret,
                accept: 'application/json',
            },
            body: JSON.stringify({ user, password }),
        });

        console.log('Respuesta de la API:', loginResponse);

        const result = await loginResponse.json();

        console.log('Cuerpo de la respuesta:', result);

        if (!loginResponse.ok) {
            return NextResponse.json(
                { message: result.message || 'Error al autenticar usuario' },
                { status: 401 }
            );
        }

        return NextResponse.json({ message: 'Autenticación exitosa' }, { status: 200 });

    } catch (error) {
        console.error('Error en el API login:', error);
        return NextResponse.json({ message: 'Error interno del servidor' }, { status: 500 });
    }
}
