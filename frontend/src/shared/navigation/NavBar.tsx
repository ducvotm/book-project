/*
The book project lets a user keep track of different books they would like to read, are currently
reading, have read or did not finish.
Copyright (C) 2020  Karan Kumar

This program is free software: you can redistribute it and/or modify it under the terms of the
GNU General Public License as published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program.
If not, see <https://www.gnu.org/licenses/>.
*/

import Button from "@material-ui/core/Button";
import { makeStyles, MuiThemeProvider, useTheme } from '@material-ui/core/styles';
import {
  ExitToApp,
  Highlight,
  MenuBook,
  Settings,
} from '@material-ui/icons';
import React from 'react';
import { Link } from 'react-router-dom';
import darkLogo from '../media/logo/dark-logo.png';
import logo from '../media/logo/logo-two-lines-white@1x.png';
import { HOME, KINDLE_HIGHLIGHTS, MY_BOOKS, SETTINGS, SIGN_IN } from '../routes';
import './NavBar.css';
  
  const useStyles = makeStyles({
    button: {
      textTransform: "none",
    },
  });

function NavItem(props: NavItemProps) {
    const classes = useStyles();
    return (
          <Link
            to={props.goTo} style={{ textDecoration: 'none' }}>
            <div className="nav-item">
                <Button className={classes.button} startIcon={props.icon}>
                      {props.itemText}
                </Button>
            </div>
         </Link>
    )
}

type NavItemProps = {
    icon: JSX.Element;
    itemText: string;
    goTo: string;
}

export function NavBar(): JSX.Element {
    const theme = useTheme();
    const navClass = 'nav-bar ' + (theme.palette.type === 'dark' ? 'nav-bar-dark' : 'nav-bar-light')

    return (
        <MuiThemeProvider theme={theme}>
        <div className={navClass}>
            <div className="nav-top">
              <Link to={HOME}>
                  <img src={theme.palette.type === 'dark' ? logo 
                  : darkLogo} alt="Logo" id="nav-bar-logo" /> 
              </Link>
            </div>
            <div className="nav-links" id="nav-links-top">
              <NavItem icon={<MenuBook />} itemText={"My Books"} goTo={MY_BOOKS} />
              <NavItem icon={<Highlight />} itemText={"Highlights"} goTo={KINDLE_HIGHLIGHTS} />
            </div>
            <div className="nav-links">
              <NavItem icon={<Settings />} itemText={"Settings"} goTo={SETTINGS} />
              <NavItem icon={<ExitToApp />} itemText={"Log out"} goTo={SIGN_IN} />
            </div>
        </div>
      </MuiThemeProvider>
    )
}
