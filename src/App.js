import Login from "./components/Login";
import logo from "./cloud.png";
import Files from "./components/Files";
import { useState } from "react";
import { Switch } from "react-router-dom";
import {
    BrowserRouter as Router,
    Link,
    Route,
    Redirect,
} from "react-router-dom";
import { Place } from "react-place";
import { useHistory } from "react-router-dom";
import { withRouter } from "react-router";
import { browserHistory } from "react-router";
import Help from "./components/Help";
import Child from "./components/Child";

import {
    FaSortAmountDown,
    FaSortAmountDownAlt,
    FaSortAlphaDown,
    FaSortAlphaDownAlt,
    FaQuestion,
    FaPlusSquare,
    FaMoon,
} from "react-icons/fa";
import AddFile from "./components/AddFile";
import nextId from "react-id-generator";
import DarkModeToggle from "react-dark-mode-toggle";

const App = () => {
    let history = useHistory();
    const [showAddFile, setShowAddFile] = useState(false);
    const [username, setUsername] = useState("User");
    // const [password, setPassword] = useState("");
    const [redirect, setRedirect] = useState(false);
    const [loggedin, setLoggedin] = useState(false);
    const [files, setFiles] = useState([]);
    const [sortBy, setSortBy] = useState("name");
    const [ascDesc, setAscDesc] = useState("asc");
    const [id, setId] = useState("id0");
    const [isDarkMode, setIsDarkMode] = useState(() => false);

    const onLogin = async (username, password) => {
        const requestOptions = {
            method: "POST",
            headers: {
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                username,
                password,
            }),
        };

        const rawResponse = await fetch(
            "http://localhost:8080/api/auth/signIn",
            requestOptions
        );

        if (rawResponse.status === 200) {
            setRedirect(true);
            setLoggedin(true);

            setFiles([
                ...files,
                {
                    id: nextId().slice(2, 20),
                    name: "filesadasdsssssad1",
                    size: 5.2,
                    location: "[52.2, 34.3]",
                    owner: "user1",
                    address: "https://picsum.photos/200",
                    type: "image",
                },
                {
                    id: nextId().slice(2, 20),
                    name: "file2",
                    size: 7.4,
                    location: "[51.2, 24.3]",
                    owner: "user2",
                    address: "https://picsum.photos/100",
                    type: "image",
                },
                {
                    id: nextId().slice(2, 20),
                    name: "aaaaaaafile2",
                    size: 100.1,
                    location: "[51.2, 24.3]",
                    owner: "user2",
                    address: "https://picsum.photos/300",
                    type: "image",
                },
                {
                    id: nextId().slice(2, 20),
                    name: "zzzzzfile2",
                    size: 1.4,
                    location: "[51.2, 24.3]",
                    owner: "user2",
                    address: "asdasdsadsaddsdadsad",
                    type: "text",
                },
            ]);
        } else alert("Wrong username or password");

        // setRedirect(true);
        // setLoggedin(true);
    };

    const onDelete = async (id) => {
        // await fetch(`http://localhost:5000/files/${id}`, {
        //     method: "DELETE",
        // });
        setFiles(files.filter((file) => file.id !== id));
    };

    const onShare = async (id) => {
        const curFile = files.find((element) => element.id === id);
        // const requestOptions = {
        //     method: "PUT",
        //     headers: {
        //         Accept: "application/json",
        //         "Content-Type": "application/json",
        //     },
        //     body: JSON.stringify({
        //         curFile,
        //     }),
        // };

        // const rawResponse = await fetch(
        //     "http://localhost:8080/api/login",
        //     requestOptions
        // );
        console.log(curFile);

        setFiles([...files]);
    };

    const onSortAlphaDown = () => {
        setSortBy("name");
        setAscDesc("asc");
        console.log(sortBy, ascDesc);
    };
    const onSortAlphaDownAlt = () => {
        setSortBy("name");
        setAscDesc("desc");
        console.log(sortBy, ascDesc);
    };
    const onSortAmountDown = () => {
        setSortBy("size");
        setAscDesc("asc");
        console.log(sortBy, ascDesc);
    };
    const onSortAmountDownAlt = () => {
        setSortBy("size");
        setAscDesc("desc");
        console.log(sortBy, ascDesc);
    };

    const onOpen = (id) => {
        console.log(id);
        setId(id);
    };

    const onRename = (value, id) => {
        // const requestOptions = {
        //     method: "PUT",
        //     headers: {
        //         Accept: "application/json",
        //         "Content-Type": "application/json",
        //     },
        //     body: JSON.stringify({
        //         curFile,
        //     }),
        // };
        // const rawResponse = await fetch(
        //     "http://localhost:8080/api/login",
        //     requestOptions
        // );

        for (var i in files) {
            if (files[i].id == id) {
                files[i].name = value;
                break; //Stop this loop, we found it!
            }
        }
        setFiles([...files]);
    };

    const onHelp = () => {};

    const onAdd = () => {
        setShowAddFile(!showAddFile);
    };

    const onAddFile = (file) => {
        // const res = await fetch("http://localhost:5000/tasks", {
        //     method: "POST",
        //     headers: {
        //         "Content-type": "application/json",
        //     },
        //     body: JSON.stringify(task),
        // });

        // const data = await res.json();

        // setTasks([...tasks, data]);

        const newFile = {
            id: nextId().slice(2, 20),
            name: file.name,
            size: 0.0,
            location: "[0.0, 0.0]",
            owner: "user1",
            address: file.cont,
            type: "text",
        };

        setFiles([...files, newFile]);
    };
    const onNight = () => {
        document.body.style.backgroundColor === "white"
            ? (document.body.style.backgroundColor = "grey")
            : (document.body.style.backgroundColor = "white");

        document.body.style.color === "black"
            ? (document.body.style.color = "white")
            : (document.body.style.color = "black");
    };

    return (
        <Router>
            <Route
                path="/"
                exact
                render={(props) => (
                    <>
                        <div className="container">
                            <div className="header">
                                {loggedin ? null : (
                                    <div className="toLogin">
                                        <Link to="/login">Login</Link>
                                    </div>
                                )}

                                {loggedin ? (
                                    <p className="loggedIn">
                                        Zalogowano jako {username}
                                    </p>
                                ) : null}

                                <div className="mainImg">
                                    <img
                                        src={logo}
                                        alt="Could not load cloud logo."
                                    />
                                </div>

                                <div className="title unselectable">
                                    File browser
                                </div>
                            </div>
                            <div className="commandBar">
                                <FaPlusSquare
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    onClick={onAdd}
                                />
                                <FaSortAlphaDown
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    onClick={onSortAlphaDown}
                                />
                                <FaSortAlphaDownAlt
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    onClick={onSortAlphaDownAlt}
                                />
                                <FaSortAmountDown
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    onClick={onSortAmountDown}
                                />
                                <FaSortAmountDownAlt
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    onClick={onSortAmountDownAlt}
                                />
                                <FaMoon
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    onClick={onNight}
                                />
                                <Link to="/help">
                                    <FaQuestion
                                        style={{
                                            color: "black",
                                            cursor: "pointer",
                                        }}
                                        onClick={onHelp}
                                    />
                                </Link>
                            </div>
                            {showAddFile && (
                                <AddFile onAddFile={onAddFile} onAdd={onAdd} />
                            )}
                            <div className="topBar">
                                <div> Name</div> <div>Size[MB]</div>{" "}
                                <div>Location</div>
                                <div>Commands</div>
                            </div>
                            <Files
                                files={files}
                                onDelete={onDelete}
                                onShare={onShare}
                                onRename={onRename}
                                sortBy={sortBy}
                                ascDesc={ascDesc}
                                onOpen={onOpen}
                            />
                            <div className="footer">
                                Total: {files.length} Files
                            </div>
                        </div>
                    </>
                )}
            />
            <Route
                path="/login"
                component={() => (
                    <>
                        <Login onLogin={onLogin} setRedirect={setRedirect} />
                        {redirect ? <Redirect push to="/"></Redirect> : null}
                    </>
                )}
            />
            <Route
                path="/help"
                component={() => (
                    <>
                        <Help onLogin={onLogin} setRedirect={setRedirect} />
                        {/* {redirect ? <Redirect push to="/"></Redirect> : null} */}
                    </>
                )}
            />
            {
                <Route
                    path="/files/:id"
                    component={() => <Child id={id} files={files}></Child>}
                ></Route>
            }
        </Router>
    );
};

export default App;
