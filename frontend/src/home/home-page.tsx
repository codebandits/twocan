import {Page} from "../layout/page";
import {Box, Button, Card, CardContent, CardMedia, Container, Grid, Typography} from "@mui/material";
import {Logo} from "../brand/logo";
import React from "react";

export const HomePage = () => (
    <Page title="Twocan">
        <Container maxWidth={false}>
            <Box textAlign="center" mb={4}>
                <Logo/>
                <Typography variant="h4" align="center" paragraph>
                    With Twocan you can find out what two can do.
                </Typography>
                <Button
                    size="large"
                    color="primary"
                    variant="contained"
                    href="/login">
                    Login
                </Button>
            </Box>
            <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                    <Card>
                        <CardMedia
                            component="img"
                            image="https://i.imgur.com/EvGEKhl.jpg"
                            title="Two Toucans"/>
                        <CardContent>
                            <Typography gutterBottom variant="h5">
                                Better Together
                            </Typography>
                            <Typography variant="body2" color="textSecondary">
                                Sometimes birds of a feather just need to flock together.
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={12} sm={6}>
                    <Card>
                        <CardMedia
                            component="img"
                            image="https://i.imgur.com/Ucv23u4.jpg"
                            title="Soaring Toucan"/>
                        <CardContent>
                            <Typography gutterBottom variant="h5">
                                Take Flight
                            </Typography>
                            <Typography variant="body2" color="textSecondary">
                                The sky is the limit. You too can soar high above!
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </Container>
    </Page>
)
