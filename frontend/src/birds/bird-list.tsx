import {Bird} from './bird'
import {
    Avatar,
    Card,
    IconButton,
    List,
    ListItem,
    ListItemAvatar,
    ListItemSecondaryAction,
    ListItemText,
    SvgIcon
} from '@material-ui/core'
import React, {useCallback} from 'react'
import Typography from '@material-ui/core/Typography'
import {ReactComponent as BirdIcon} from './bird.svg'
import {ReactComponent as FlightIcon} from './flight.svg'
import {useAddFlight} from "./add-flight";

type Props = {
    birds: Bird[]
}

export const BirdList = ({birds}: Props) => {
    if (birds.length > 0) {
        return (
            <Card>
                <List>
                    {birds.map(bird => <BirdListItem key={bird.id} bird={bird}/>)}
                </List>
            </Card>
        )
    } else {
        return <Typography align="center">Don't you know any birds? Jump out of the nest and add them!</Typography>
    }
}

type BirdListItemProps = {
    bird: Bird
}

const BirdListItem = ({bird}: BirdListItemProps) => {
    const addFlight = useAddFlight()
    const onAddFlight = useCallback(() => addFlight({birdId: bird.id}), [addFlight, bird.id])
    return (
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
            <ListItemSecondaryAction>
                <IconButton edge="end" aria-label="flight" onClick={onAddFlight}>
                    <SvgIcon>
                        <FlightIcon/>
                    </SvgIcon>
                </IconButton>
            </ListItemSecondaryAction>
        </ListItem>
    );
}
