'use client';

import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import {
    Snackbar,
    Alert,
    IconButton,
    CircularProgress,
    TextField,
    Button,
    Grid,
    Card,
    CardContent,
    Typography,
    Accordion,
    AccordionSummary,
    AccordionDetails,
    Tooltip,
    Box,
    Fade, Skeleton,
} from '@mui/material';
import {
    ContentCopy,
    Visibility,
    VisibilityOff,
    CheckCircle,
    Error as ErrorIcon,
    ExpandMore,
} from '@mui/icons-material';
import { styled } from '@mui/system';

const Pre = styled('pre')(({ theme }) => ({
    backgroundColor: theme.palette.grey[200],
    padding: theme.spacing(2),
    borderRadius: theme.shape.borderRadius,
    overflowX: 'auto',
    fontSize: '1rem',
}));

export const AppContent = () => {
    const router = useRouter();
    const [selectedEndpoint, setSelectedEndpoint] = useState<string>('auth');
    const [user, setUser] = useState('');
    const [password, setPassword] = useState('');
    const [apiKey, setApiKey] = useState<string | null>(null);
    const [apiSecret, setApiSecret] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);
    const [responseMessage, setResponseMessage] = useState<string | null>(null);
    const [userResponse, setUserResponse] = useState<any>(null);
    const [statusCode, setStatusCode] = useState<number | null>(null);
    const [responseTime, setResponseTime] = useState<number | null>(null);
    const [isClient, setIsClient] = useState(false);
    const [showSecret, setShowSecret] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState<string | null>(null);
    const [snackbarSeverity, setSnackbarSeverity] = useState<'success' | 'error'>('error');

    useEffect(() => {
        setIsClient(true);

        if (typeof window !== 'undefined') {
            const storedApiKey = localStorage.getItem('apiKey');
            const storedApiSecret = localStorage.getItem('apiSecret');

            if (storedApiKey && storedApiSecret) {
                setApiKey(storedApiKey);
                setApiSecret(storedApiSecret);
            } else {
                router.push('/');
            }
        }
    }, [router]);

    const exampleCurl = `curl -X 'POST' \\
  'http://localhost:3000/api/auth' \\
  -H 'accept: application/json' \\
  -H 'x-api-key-id: ${apiKey || "<API_KEY>"}' \\
  -H 'x-api-secret: ${apiSecret || "<API_SECRET>"}' \\
  -H 'Content-Type: application/json' \\
  -d '{ "user": "username", "password": "password" }'`;

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (loading) return;
        setLoading(true);

        setUserResponse(null);
        setResponseMessage(null);
        setStatusCode(null);
        setResponseTime(null);

        const startTime = performance.now();

        try {
            if (!apiKey || !apiSecret) {
                throw new Error('API Key o API Secret no encontrados');
            }

            const body = { user, password };

            const response = await fetch(`/api/${selectedEndpoint}`, {
                method: 'POST',
                headers: {
                    'x-api-key-id': apiKey,
                    'x-api-secret': apiSecret,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(body),
            });

            const result = await response.json();

            const endTime = performance.now();
            const time = endTime - startTime;

            setUserResponse(result);
            setStatusCode(response.status);
            setResponseTime(time);

            if (response.ok) {
                setResponseMessage('Solicitud exitosa');
            } else {
                setResponseMessage(result.message || 'Error al procesar la solicitud');
            }
        } catch (error: any) {
            setResponseMessage(error.message || 'Error al procesar la solicitud');
        } finally {
            setLoading(false);
        }
    };

    const handleCopy = (text: string) => {
        navigator.clipboard.writeText(text).then(() => {
            setSnackbarMessage('Texto copiado al portapapeles');
            setSnackbarSeverity('success');
        }).catch(() => {
            setSnackbarMessage('Error al copiar el texto');
            setSnackbarSeverity('error');
        });
    };

    const handleCloseSnackbar = () => {
        setSnackbarMessage(null);
    };

    if (!isClient) {
        return null;
    }

    return (
        <Box
            sx={{
                background: 'linear-gradient(135deg, #ece9e6 0%, #ffffff 100%)',
                minHeight: '100vh',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                padding: 2,
            }}
        >
            <Fade in={isClient}>
                <Card sx={{ maxWidth: 1200, width: '100%', p: 4, boxShadow: 3, borderRadius: 3 }}>
                    <Typography variant="h4" component="h1" align="center" color="primary" gutterBottom>
                        SUM API
                    </Typography>

                    <Accordion defaultExpanded>
                        <AccordionSummary expandIcon={<ExpandMore />}>
                            <Typography variant="h6">Probar Endpoint: Autenticación</Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                            <Grid container spacing={4}>
                                <Grid item xs={12}>
                                    <Card variant="outlined">
                                        <CardContent>
                                            <Typography variant="h6" gutterBottom>
                                                Ejemplo de cURL
                                            </Typography>
                                            <Box position="relative">
                                                <Pre>
                                                    {exampleCurl}
                                                </Pre>
                                                <Tooltip title="Copiar cURL">
                                                    <IconButton
                                                        onClick={() => handleCopy(exampleCurl)}
                                                        sx={{
                                                            position: 'absolute',
                                                            top: 8,
                                                            right: 8,
                                                            color: 'primary.main',
                                                        }}
                                                    >
                                                        <ContentCopy />
                                                    </IconButton>
                                                </Tooltip>
                                            </Box>
                                        </CardContent>
                                    </Card>
                                </Grid>


                                {apiKey && apiSecret && (
                                    <Grid item xs={12}>
                                        <Card variant="outlined">
                                            <CardContent>
                                                <Typography variant="h6" gutterBottom>
                                                    Headers
                                                </Typography>
                                                <Grid container spacing={2} alignItems="center">
                                                    <Grid item xs={12} sm={6}>
                                                        <TextField
                                                            label="x-api-key-id"
                                                            fullWidth
                                                            value={apiKey}
                                                            InputProps={{
                                                                readOnly: true,
                                                                endAdornment: (
                                                                    <Tooltip title="Copiar API Key">
                                                                        <IconButton onClick={() => handleCopy(apiKey)}>
                                                                            <ContentCopy />
                                                                        </IconButton>
                                                                    </Tooltip>
                                                                ),
                                                            }}
                                                            variant="outlined"
                                                        />
                                                    </Grid>

                                                    <Grid item xs={12} sm={6}>
                                                        <TextField
                                                            label="x-api-secret"
                                                            fullWidth
                                                            type={showSecret ? 'text' : 'password'}
                                                            value={apiSecret}
                                                            InputProps={{
                                                                readOnly: true,
                                                                endAdornment: (
                                                                    <Box display="flex" alignItems="center">
                                                                        <Tooltip title={showSecret ? "Ocultar" : "Mostrar"}>
                                                                            <IconButton onClick={() => setShowSecret(!showSecret)}>
                                                                                {showSecret ? <VisibilityOff /> : <Visibility />}
                                                                            </IconButton>
                                                                        </Tooltip>
                                                                        <Tooltip title="Copiar API Secret">
                                                                            <IconButton onClick={() => handleCopy(apiSecret)}>
                                                                                <ContentCopy />
                                                                            </IconButton>
                                                                        </Tooltip>
                                                                    </Box>
                                                                ),
                                                            }}
                                                            variant="outlined"
                                                        />
                                                    </Grid>
                                                </Grid>
                                            </CardContent>
                                        </Card>
                                    </Grid>
                                )}

                                <Grid item xs={12} md={6}>
                                    <Card variant="outlined">
                                        <CardContent>
                                            <Typography variant="h6" gutterBottom>
                                                Request Body
                                            </Typography>
                                            <Box component="form" onSubmit={handleSubmit}>
                                                <TextField
                                                    label="user"
                                                    fullWidth
                                                    value={user}
                                                    onChange={(e) => setUser(e.target.value)}
                                                    required
                                                    variant="outlined"
                                                    margin="normal"
                                                />
                                                <TextField
                                                    label="password"
                                                    fullWidth
                                                    type="password"
                                                    value={password}
                                                    onChange={(e) => setPassword(e.target.value)}
                                                    required
                                                    variant="outlined"
                                                    margin="normal"
                                                />
                                                <Button
                                                    variant="contained"
                                                    color="primary"
                                                    type="submit"
                                                    fullWidth
                                                    sx={{ mt: 2, '&:hover': { backgroundColor: 'primary.dark' } }}
                                                    disabled={loading}
                                                >
                                                    {loading ? <CircularProgress size={24} color="inherit" /> : 'Enviar Solicitud'}
                                                </Button>
                                            </Box>
                                        </CardContent>
                                    </Card>
                                </Grid>

                                <Grid item xs={12} md={6}>
                                    <Card variant="outlined">
                                        <CardContent>
                                            <Typography variant="h6" gutterBottom>
                                                Response
                                            </Typography>

                                            {/* Mostrar plantilla "vacía" si no se ha realizado la petición */}
                                            {!userResponse ? (
                                                <>
                                                    <Skeleton variant="text" width="60%" height={30} />
                                                    <Skeleton variant="text" width="40%" height={25} />
                                                    <Skeleton variant="rectangular" width="100%" height={150} sx={{ mt: 2 }} />
                                                </>
                                            ) : (
                                                <>
                                                    {statusCode && (
                                                        <Typography variant="body1" color={statusCode === 200 ? 'green' : 'red'}>
                                                            {`HTTP Status: ${statusCode}`}
                                                        </Typography>
                                                    )}
                                                    {responseTime && (
                                                        <Typography variant="body2" color="textSecondary">
                                                            {`Response Time: ${responseTime.toFixed(2)} ms`}
                                                        </Typography>
                                                    )}
                                                    <Pre>{JSON.stringify(userResponse, null, 2)}</Pre>
                                                    {responseMessage && (
                                                        <Box display="flex" alignItems="center" mt={2}>
                                                            <Typography variant="body1" sx={{ color: responseMessage === 'Solicitud exitosa' ? 'green' : 'red' }}>
                                                                {responseMessage}
                                                            </Typography>
                                                            {responseMessage === 'Solicitud exitosa' ? (
                                                                <CheckCircle sx={{ ml: 1, color: 'green' }} />
                                                            ) : (
                                                                <ErrorIcon sx={{ ml: 1, color: 'red' }} />
                                                            )}
                                                        </Box>
                                                    )}
                                                </>
                                            )}
                                        </CardContent>
                                    </Card>
                                </Grid>
                            </Grid>
                        </AccordionDetails>
                    </Accordion>

                    <Snackbar
                        open={!!snackbarMessage}
                        autoHideDuration={3000}
                        onClose={handleCloseSnackbar}
                        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
                    >
                        <Alert onClose={handleCloseSnackbar} severity={snackbarSeverity} sx={{ width: '100%' }}>
                            {snackbarMessage}
                        </Alert>
                    </Snackbar>
                </Card>
            </Fade>
        </Box>
    );
};
