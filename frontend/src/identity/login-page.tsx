import {Box, Button, Card, CardContent, Container, TextField, Typography} from '@material-ui/core'
import {Page} from '../layout/page'
import React from 'react'
import {useFormData} from '../form'
import {Logo} from "../logo";
import {useLogin} from './login'

const initialValues = {emailAddress: ''}

export const LoginPage = () => {

    const login = useLogin()
    const formData = useFormData({
        initialValues: initialValues,
        submit: login,
    })

    return (
        <Page title="Twocan">
            <Container>
                <Box display="flex" justifyContent="center" mt={2}>
                    <Card>
                        <CardContent>
                            <Typography variant="h4" align="center">Login</Typography>
                            <Box textAlign="center"><Logo/></Box>
                            <form onSubmit={formData.onSubmit}>
                                <TextField
                                    {...formData.fields.emailAddress.textFieldProps}
                                    autoFocus
                                    id="login-email-address-input"
                                    label="Email Address"
                                    fullWidth/>
                                <Button
                                    {...formData.submitButtonProps}
                                    size="large"
                                    color="primary"
                                    variant="contained"
                                    fullWidth>
                                    Login
                                </Button>
                            </form>
                        </CardContent>
                    </Card>
                </Box>
            </Container>
        </Page>
    )
}
