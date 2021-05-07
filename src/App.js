import Login from "./components/Login";
import SignUp from "./components/SignUp";
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
import Popup from "reactjs-popup";
import { useCookies } from "react-cookie";
import axios from "axios";
import * as _ from "lodash";

const App = () => {
    let history = useHistory();
    const [showAddFile, setShowAddFile] = useState(false);
    const [username, setUsername] = useState("User");
    // const [password, setPassword] = useState("");
    const [redirect, setRedirect] = useState(false);
    const [loggedin, setLoggedin] = useState(false);
    const [popupOpen, setPopupOpen] = useState(false);
    const [files, setFiles] = useState([]);
    const [sortBy, setSortBy] = useState("name");
    const [ascDesc, setAscDesc] = useState("asc");
    const [id, setId] = useState("id0");
    const [isDarkMode, setIsDarkMode] = useState(() => false);
    const [cookies, setCookie] = useCookies(["name"]);
    const [backup, setBackup] = useState();

    //
    // On login
    //

    const onLogin = async (username, password) => {
        // Set a sign in request.
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
            credentials: "include",
        };

        const rawResponse = await fetch(
            "http://localhost:8080/api/auth/signIn",
            requestOptions
        );

        // If login was successful.
        if (rawResponse.status === 200) {
            setRedirect(true);
            setLoggedin(true);
            setUsername(username);

            // Get filenames.
            const requestOptions2 = {
                method: "GET",
                headers: {
                    Accept: "application/json",
                    "Content-Type": "application/json",
                },
                credentials: "include",
            };

            var arr = [];
            var folders = [];
            const rawResponse2 = await fetch(
                "http://localhost:8080/api/fileDrop",
                requestOptions2
            )
                .then((response) => {
                    return response.json();
                })
                .then((data) => {
                    for (var i in data) {
                        for (var j = 0; j < data[i].length; j++) {
                            if (i.startsWith(username)) {
                                if (i === username) {
                                    arr.push({
                                        id: nextId().slice(2, 20),
                                        name: data[i][j],
                                        size: 5.2,
                                        location: "[52.2, 34.3]",
                                        owner: "user1",
                                        address: "https://picsum.photos/200",
                                        type: "text",
                                    });
                                } else if (
                                    !folders.includes(
                                        i.substr(
                                            username.length + 1,
                                            i.length - 1
                                        )
                                    )
                                ) {
                                    var lArr = [];
                                    var myId = nextId().slice(2, 20);
                                    lArr.push({
                                        id: myId,
                                        name: i.substr(
                                            username.length + 1,
                                            i.length - 1
                                        ),
                                        size: 0,
                                        location: "-",
                                        owner: "user1",
                                        address: "https://picsum.photos/200",
                                        type: "dir",
                                    });
                                    for (var i in data) {
                                        for (
                                            var j = 0;
                                            j < data[i].length;
                                            j++
                                        ) {
                                            var tmp =
                                                username +
                                                "/" +
                                                i.substr(
                                                    username.length + 1,
                                                    i.length - 1
                                                );
                                            if (i.startsWith(tmp)) {
                                                lArr.push({
                                                    id: nextId().slice(2, 20),
                                                    name: data[i][j],
                                                    size: 5.2,
                                                    location: "[52.2, 34.3]",
                                                    owner: "user1",
                                                    address:
                                                        "https://picsum.photos/200",
                                                    type: "text",
                                                });
                                            }
                                        }
                                    }

                                    folders.push(
                                        i.substr(
                                            username.length + 1,
                                            i.length - 1
                                        )
                                    );
                                    arr.push(lArr);
                                }
                            }
                        }
                    }
                });
            console.log(arr);
            setFiles(arr);
            setBackup(arr);
        } else alert("Wrong username or password");
    };

    //
    //
    //

    //
    // On delete account
    //
    const deleteAccount = async () => {
        const requestOptions = {
            method: "GET",
            headers: {
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            // body: JSON.stringify({}),
        };
        const rawResponse = await fetch(
            "http://localhost:8080/api/auth/delete",
            requestOptions
        );

        if (rawResponse.status === 200) {
            setRedirect(false);
            setLoggedin(false);

            setFiles([]);
        } else alert("Cannot delete account");
    };

    const onLogOut = async () => {
        const requestOptions = {
            method: "GET",
            headers: {
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            credentials: "include",
            // body: JSON.stringify({}),
        };

        const rawResponse = await fetch(
            "http://localhost:8080/api/auth/logOut",
            requestOptions
        );

        if (rawResponse.status === 200) {
            setRedirect(false);
            setLoggedin(false);

            setFiles([]);
        } else alert("Cannot log out");
    };

    const onSignUp = async (username, email, password, dob) => {
        const requestOptions = {
            method: "POST",
            headers: {
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                username,
                email,
                password,
                dob,
            }),
        };

        const rawResponse = await fetch(
            "http://localhost:8080/api/auth/signUp",
            requestOptions
        );

        if (rawResponse.status === 200) {
            setRedirect(true);
            setLoggedin(true);

            // setFiles([
            //     ...files,
            //     {
            //         id: nextId().slice(2, 20),
            //         name: "filesadasdsssssad1",
            //         size: 5.2,
            //         location: "[52.2, 34.3]",
            //         owner: "user1",
            //         address: "https://picsum.photos/200",
            //         type: "image",
            //     },
            //     {
            //         id: nextId().slice(2, 20),
            //         name: "file2",
            //         size: 7.4,
            //         location: "[51.2, 24.3]",
            //         owner: "user2",
            //         address: "https://picsum.photos/100",
            //         type: "image",
            //     },
            //     {
            //         id: nextId().slice(2, 20),
            //         name: "aaaaaaafile2",
            //         size: 100.1,
            //         location: "[51.2, 24.3]",
            //         owner: "user2",
            //         address: "https://picsum.photos/300",
            //         type: "image",
            //     },
            //     {
            //         id: nextId().slice(2, 20),
            //         name: "zzzzzfile2",
            //         size: 1.4,
            //         location: "[51.2, 24.3]",
            //         owner: "user2",
            //         address: "asdasdsadsaddsdadsad",
            //         type: "text",
            //     },
            // ]);
        } else alert("Wrong username, email, password, or dob");

        // setRedirect(true);
        // setLoggedin(true);
    };

    const onFolder = (file) => {
        //file.shift();

        setBackup(_.cloneDeep(files));
        file[0].name = "..";
        setFiles(file);
    };

    const onRestore = () => {
        setFiles(backup);
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
            name: file.file.name,
            size: 0.0,
            location: "[0.0, 0.0]",
            owner: "user1",
            address: "",
            type: "text",
        };

        const data = new FormData();
        data.append("files", file.file);
        data.append("dir", "test");

        axios
            .post("http://localhost:8080/api/fileDrop", data, {
                withCredentials: true,
            })
            .then((res) => {
                console.log(res.statusText);
            });

        // const requestOptions = {
        //     method: "POST",
        //     headers: {
        //         Accept: "application/json",
        //         "Content-Type": "application/json",
        //     },
        //     body: JSON.stringify({
        //         username,
        //         password,
        //     }),
        //     credentials: "include",
        // };

        // const rawResponse = await fetch(
        //     "http://localhost:8080/api/fileDrop",
        //     requestOptions
        // );

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
                                        <div>
                                            <Link to="/signup">Sign Up</Link>
                                        </div>
                                        <Link to="/login">Login</Link>
                                    </div>
                                )}

                                {loggedin ? (
                                    <p className="loggedIn">
                                        Zalogowano jako {username}
                                        <div>
                                            <button
                                                className="noDeleteButton"
                                                onClick={onLogOut}
                                            >
                                                Log out
                                            </button>
                                            <Popup
                                                open={popupOpen}
                                                trigger={
                                                    <button className="deleteButton">
                                                        {" "}
                                                        Usuń Konto
                                                    </button>
                                                }
                                                position="right center"
                                            >
                                                <div>
                                                    Are you sure?
                                                    <button
                                                        className="deleteButton"
                                                        onClick={deleteAccount}
                                                    >
                                                        {" "}
                                                        Yes
                                                    </button>
                                                </div>
                                            </Popup>
                                        </div>
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
                                <div>Coordinates</div>
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
                                onFolder={onFolder}
                                onRestore={onRestore}
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
                path="/signup"
                component={() => (
                    <>
                        <SignUp onSignUp={onSignUp} setRedirect={setRedirect} />
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
