'use client';

import React, { useState } from 'react';
import { TextField, Button, InputAdornment, Box, Snackbar, Alert } from '@mui/material';
import UnmsmIcon from './UnmsmIcon';
import { createUser } from "@/actions/create";
import { useRouter } from 'next/navigation';

export const CreateUserForm = () => {
    const router = useRouter();
    const [email, setEmail] = useState('');
    const [username, setUsername] = useState('');
    const [institutionId, setInstitutionId] = useState('');
    const [loading, setLoading] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState<string | null>(null);
    const [snackbarSeverity, setSnackbarSeverity] = useState<'success' | 'error'>('error');

    const handleCreateUser = async (e: React.FormEvent) => {
        e.preventDefault();
        if (loading) return;
        setLoading(true);

        try {
            const result = await createUser({
                username,
                email: email + '@unmsm.edu.pe',
                institution_id: institutionId,
            });

            console.log('Respuesta de la API:', result);

            if (!result.success) {
                setSnackbarSeverity('error');
                setSnackbarMessage(result.message || 'Error al crear el usuario');
                setLoading(false);
                return;
            }

            localStorage.setItem('apiKey', result.apiKeyId);
            localStorage.setItem('apiSecret', result.apiSecret);

            setSnackbarSeverity('success');
            setSnackbarMessage('Usuario creado exitosamente. Redirigiendo...');
            router.push('/app');
        } catch (error) {
            console.error('Error procesando la solicitud:', error);
            setSnackbarSeverity('error');
            setSnackbarMessage('Error procesando la solicitud');
        } finally {
            setLoading(false);
        }
    };

    const handleCloseSnackbar = () => {
        setSnackbarMessage(null);
    };

    function removeAccents(text: string) {
        return text.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
    }

    return (
        <>
            <Box
                display="flex"
                flexDirection="column"
                justifyContent="flex-start"
                alignItems="center"
                sx={{
                    height: '100vh',
                    backgroundColor: '#FFFFFF',
                    padding: '0',
                    margin: '0',
                    '@media (min-width: 1024px)': {
                        flexDirection: 'row',
                        justifyContent: 'center',
                    },
                }}
            >
                <Box
                    display="flex"
                    flexDirection="column"
                    alignItems="center"
                    sx={{
                        marginTop: '40px',
                        '@media (min-width: 1024px)': {
                            flexDirection: 'row',
                            marginTop: '0',
                        },
                    }}
                >
                    <Box sx={{
                        marginBottom: '20px',
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                    }}>
                        <UnmsmIcon width={300} height={300} />
                    </Box>

                    <Box
                        component="form"
                        onSubmit={handleCreateUser}
                        sx={{
                            width: '100%',
                            maxWidth: '400px',
                            padding: '0 20px',
                        }}
                    >
                        <TextField
                            label="Nombre de usuario"
                            fullWidth
                            value={username}
                            onChange={(e) => setUsername(removeAccents(e.target.value).trim())}
                            required
                            sx={{
                                marginBottom: '12px',
                                backgroundColor: '#E0E0E0',
                                borderRadius: '8px',
                            }}
                        />

                        <TextField
                            label="Correo electrónico"
                            fullWidth
                            value={email}
                            onChange={(e) => setEmail(removeAccents(e.target.value).trim().toLowerCase())}
                            required
                            sx={{
                                marginBottom: '12px',
                                backgroundColor: '#E0E0E0',
                                borderRadius: '8px',
                            }}
                            slotProps={{
                                input: {
                                    endAdornment: (
                                        <InputAdornment position="end">@unmsm.edu.pe</InputAdornment>
                                    ),
                                }
                            }}
                        />

                        <TextField
                            label="Código de estudiante"
                            fullWidth
                            value={institutionId}
                            onChange={(e) => setInstitutionId(e.target.value)}
                            required
                            sx={{
                                marginBottom: '12px',
                                backgroundColor: '#E0E0E0',
                                borderRadius: '8px',
                            }}
                        />

                        <Button
                            variant="contained"
                            color="primary"
                            type="submit"
                            fullWidth
                            disabled={loading}
                            sx={{
                                padding: '10px',
                                backgroundColor: '#621518',
                                '&:hover': { backgroundColor: '#400000' },
                            }}
                        >
                            {loading ? 'Creando cuenta...' : 'Crear cuenta'}
                        </Button>
                    </Box>
                </Box>
            </Box>

            <Snackbar
                open={!!snackbarMessage}
                autoHideDuration={6000}
                onClose={handleCloseSnackbar}
                anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
            >
                <Alert onClose={handleCloseSnackbar} severity={snackbarSeverity} sx={{ width: '100%' }}>
                    {snackbarMessage}
                </Alert>
            </Snackbar>
        </>
    );
};
