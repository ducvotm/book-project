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

import { ThemeProvider } from '@material-ui/core/styles';
import 'bootstrap/dist/css/bootstrap.min.css';
import React, { useState } from 'react';
import {
    BrowserRouter,
    Route,
} from "react-router-dom";
import KindleHighlights from "./kindle-highlights/KindleHighlights";
import Login from "./login/Login";
import MyBooks from "./my-books/MyBooks";
import Register from "./register/Register";
import Settings from "./settings/Settings";
import {
    HOME,
    KINDLE_HIGHLIGHTS,
    MY_BOOKS,
    SETTINGS,
    SIGN_IN,
    SIGN_UP
} from "./shared/routes";
import { darkTheme, theme as lightTheme } from './shared/theme';

function App(): JSX.Element {
    const [theme, setTheme] = useState(lightTheme);

    function toggleTheme(): void {
        theme === lightTheme ? setTheme(darkTheme) : setTheme(lightTheme)
    } 
    return (
        <ThemeProvider theme={theme}>
          <BrowserRouter>
              <Route exact path={HOME} component={Login} />
              <Route path={SIGN_IN} component={Login} />
              <Route path={SIGN_UP} component={Register} />
              <Route path={MY_BOOKS} component={MyBooks} />
              <Route 
                path={SETTINGS} 
                render={() => 
                <Settings  theme={theme} toggleTheme={toggleTheme} />} 
              />
              <Route path={KINDLE_HIGHLIGHTS} component={KindleHighlights} />
          </BrowserRouter>
        </ThemeProvider>
    )
}

export default App;