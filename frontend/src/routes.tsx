import {DrawerLayout} from "./layout/drawer";
import {Route, Routes} from "react-router-dom";
import {InboxPage, MailPage} from "./sample-pages";
import React from "react";
import {HomePage} from "./home/home-page";

export const AppRoutes = () => (
    <Routes>
        <Route path="/" element={<DrawerLayout/>}>
            <Route index element={<HomePage/>}/>
            <Route path="mail" element={<MailPage/>}/>
            <Route path="inbox" element={<InboxPage/>}/>
        </Route>
    </Routes>
)
