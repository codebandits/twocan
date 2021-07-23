import React from 'react'
import Typography from '@material-ui/core/Typography'
import {Page} from '../layout/page'
import {Logo} from '../logo'
import {
    Avatar,
    Box,
    Button,
    Card,
    Container,
    List,
    ListItem,
    ListItemAvatar,
    ListItemText,
    SvgIcon
} from '@material-ui/core'
import {ReactComponent as BirdIcon} from './bird.svg'
import {Bird, useBirds} from "./bird";
import {useDialogState} from "../dialog";
import {AddBirdDialog} from "./add-bird-dialog";
import {isFetchGetResponseOk} from '../fetch/fetch-get'

export const BirdsPage = () => {
    const {
        open: addBirdOpen,
        handleOpen: handleAddBirdOpen,
        handleClose: handleAddBirdClose,
    } = useDialogState()
    const {data: birdsResponse} = useBirds()

    if (birdsResponse === undefined) {
        return null
    } else if (isFetchGetResponseOk(birdsResponse)) {
        return (
            <>
                <Page title="Twocan">
                    <Container maxWidth="md">
                        <Box textAlign="center" mt={2}><Logo/></Box>
                        <Typography variant="h4" align="center">Birds</Typography>
                        <Box textAlign="right" my={2}>
                            <Button
                                color="primary"
                                variant="contained"
                                onClick={handleAddBirdOpen}>
                                Add
                            </Button>
                        </Box>
                        <Card>
                            <List>
                                {birdsResponse.data.map(bird => <BirdListItem key={bird.id} bird={bird}/>)}
                            </List>
                        </Card>
                    </Container>
                </Page>

                <AddBirdDialog open={addBirdOpen} handleClose={handleAddBirdClose}/>
            </>
        )
    } else {
        return null
    }
}

type BirdListItemProps = {
    bird: Bird
}

const BirdListItem = ({bird}: BirdListItemProps) => (
    <ListItem divider>
        <ListItemAvatar>
            <Avatar>
                <SvgIcon>
                    <BirdIcon/>
                </SvgIcon>
            </Avatar>
        </ListItemAvatar>
        <ListItemText
            primary={`${bird.firstName} ${bird.lastName}`.trim()}
            secondary="come fly with me"/>
    </ListItem>
)
