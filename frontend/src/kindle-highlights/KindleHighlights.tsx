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

import React, { Component, ReactElement } from "react";
import Endpoints from '../shared/api/endpoints';
import { Layout } from "../shared/components/Layout";
import HttpClient from '../shared/http/HttpClient';
import { NavBar } from "../shared/navigation/NavBar";
import { KindleHighlight } from '../shared/types/KindleHighlight';
import "./KindleHighlights.css";

interface IState {
    highlights: KindleHighlight[];
    loading: boolean;
}

class KindleHighlights extends Component<Record<string, unknown>, IState> {
    constructor(props: Record<string, unknown>) {
        super(props);
        this.state = {
            highlights: [],
            loading: true
        };
        this.getHighlights = this.getHighlights.bind(this);
    }

    componentDidMount(): void {
        this.getHighlights();
    }

    getHighlights(): void {
        this.setState({ loading: true });
        HttpClient.get(Endpoints.kindleHighlights)
            .then((highlights: KindleHighlight[]) => {
                this.setState({
                    highlights: Array.isArray(highlights) ? highlights : [],
                    loading: false
                });
            })
            .catch((error: Record<string, string>) => {
                console.error('Error fetching highlights: ', error);
                if (error instanceof Response) {
                    error.json().then((body: Record<string, string>) => {
                        console.error('Error response body: ', body);
                    }).catch(() => {
                        console.error('Error status: ', error.status, error.statusText);
                    });
                }
                this.setState({
                    loading: false
                });
            });
    }

    groupHighlightsByBook(): Map<string, KindleHighlight[]> {
        const grouped = new Map<string, KindleHighlight[]>();
        this.state.highlights.forEach(highlight => {
            const key = `${highlight.title}|||${highlight.author}`;
            if (!grouped.has(key)) {
                grouped.set(key, []);
            }
            grouped.get(key)?.push(highlight);
        });
        return grouped;
    }

    render(): ReactElement {
        const { highlights, loading } = this.state;

        if (loading) {
            return (
                <Layout title="Kindle Highlights" btn={<div />}>
                    <NavBar />
                    <div className="kindle-highlights-loading">
                        Loading highlights...
                    </div>
                </Layout>
            );
        }

        if (highlights.length === 0) {
            return (
                <Layout title="Kindle Highlights" btn={<div />}>
                    <NavBar />
                    <div className="kindle-highlights-empty">
                        <p>No highlights found.</p>
                        <p>Import your Kindle highlights using the import script.</p>
                    </div>
                </Layout>
            );
        }

        const groupedHighlights = this.groupHighlightsByBook();

        return (
            <Layout title="Kindle Highlights" btn={<div />}>
                <NavBar />
                <div className="kindle-highlights-container">
                    {Array.from(groupedHighlights.entries()).map(([key, bookHighlights]) => {
                        const [title, author] = key.split('|||');
                        return (
                            <div key={key} className="kindle-highlight-book-group">
                                <div className="kindle-highlight-book-header">
                                    <h2 className="kindle-highlight-book-title">{title}</h2>
                                    <p className="kindle-highlight-book-author">by {author}</p>
                                </div>
                                <div className="kindle-highlight-list">
                                    {bookHighlights.map((highlight) => (
                                        <div key={highlight.id} className="kindle-highlight-item">
                                            <div className="kindle-highlight-content">
                                                {highlight.content}
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        );
                    })}
                </div>
            </Layout>
        );
    }
}

export default KindleHighlights;

