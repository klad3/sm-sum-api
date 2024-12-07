'use server';

const apiBaseUrl: string = process.env.API_BASE_URL!;


export interface ResponseApiSum {
    "success": boolean,
    "message": string,
    "data": {
        "username": string,
        "email": string,
        "institutionId": string,
        "studentName": string,
        "apiKey": string,
        "apiSecret": string,
        "message": string
    }
}

export async function createUser({ username, email, institution_id }: { username: string; email: string; institution_id: string }): Promise<{ success: boolean; message?: string; apiKeyId?: string; apiSecret?: string }> {
    if (!username || !email || !institution_id) {
        return { success: false, message: 'Faltan parámetros requeridos (username, email, institution_id)' };
    }

    try {
        // Realizar la solicitud POST al endpoint /user/create
        const createUserResponse: Response = await fetch(`${apiBaseUrl}/user/create`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, email, institutionId: institution_id, studentName: "Usuario" }),
        });

        // Logueamos la respuesta completa de la API
        console.log('Respuesta de la API:', createUserResponse);

        // Verificación del estado de la respuesta
        if (!createUserResponse.ok) {
            if (createUserResponse.status === 400) {
                return { success: false, message: 'Error al crear el usuario: parámetros incorrectos' };
            }
            return { success: false, message: 'Error al crear el usuario' };
        }

        // Procesar la respuesta del servidor
        const responseBody: ResponseApiSum = await createUserResponse.json();

        // Logueamos el cuerpo de la respuesta JSON
        console.log('Cuerpo de la respuesta:', responseBody);

        const { success, message, data } = responseBody;

        // Verificar si el mensaje de respuesta es el esperado
        if (success && message === 'User created successfully') {
            return {
                success: true,
                apiKeyId: data.apiKey,
                apiSecret: data.apiSecret,
            };
        }

        return { success: false, message: 'Error desconocido al crear el usuario' };
    } catch (error) {
        // Logueamos el error en caso de fallo de la solicitud
        console.error('Error procesando la solicitud:', error);
        return { success: false, message: 'Error interno del servidor' };
    }
}
