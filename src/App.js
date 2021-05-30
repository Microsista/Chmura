// Custom components
import Login from "./components/Login";
import SignUp from "./components/SignUp";
import Files from "./components/Files";
import AddFile from "./components/AddFile";
import Help from "./components/Help";
import Child from "./components/Child";

// Resources
import logo from "./cloud.png";

// External libraries
import { useState } from "react";
import {
    BrowserRouter as Router,
    Link,
    Route,
    Redirect,
    useHistory,
} from "react-router-dom";
import {
    FaSortAmountDown,
    FaSortAmountDownAlt,
    FaSortAlphaDown,
    FaSortAlphaDownAlt,
    FaQuestion,
    FaPlusSquare,
    FaMoon,
} from "react-icons/fa";
import nextId from "react-id-generator";
import Popup from "reactjs-popup";
import axios from "axios";
import * as _ from "lodash";
import { useEffect } from "react";

const App = () => {
    var history = useHistory();
    //
    // Callbacks
    //

    const onGoBack = () => {
        setDummy(dummy + 1);
    };

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

        var mytoken;
        await fetch("http://localhost:8080/api/auth/signIn", requestOptions)
            .then((response) => response.json())
            .then((data) => {
                mytoken = data.token;
            });

        const rawResponse = await fetch(
            "http://localhost:8080/api/auth/signIn",
            requestOptions
        );

        // If login was successful.
        if (rawResponse.status === 200) {
            setRedirect(true);
            setLoggedin(true);
            setUsername(username);

            var mystring = "Bearer " + mytoken;
            setToken(mystring);

            const options = {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    Accept: "application/json",
                    Authorization: mystring,
                },
            };

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Initial fetch of the data to iterate over and get file sizes.
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////

            var fetchedData;

            await fetch("http://localhost:8080/api/fileDrop", options)
                .then((response) => response.json())
                .then((outerdata) => {
                    fetchedData = outerdata;
                });

            // get array size///////////////////////////////

            var k = 0;
            // ile jest lokalizacji moich i wspoldzielonych?
            for (var i in fetchedData.myFiles) k++;

            for (var i in fetchedData.sharedFiles) k++;

            // utworz tablice z iloscia elementow rowna ilosci lokalizacji
            var sizes = new Array(k);
            var locations = new Array(k);

            // dla kazdej z tych lokalizacji utworz tablice o rozmiarze tyle ile jest w niej plikow
            for (var i in fetchedData.myFiles) {
                sizes[i] = new Array(fetchedData.myFiles[i].length);
                locations[i] = new Array(fetchedData.myFiles[i].length);
            }
            for (var i in fetchedData.sharedFiles) {
                sizes[i] = new Array(fetchedData.sharedFiles[i].length);
                locations[i] = new Array(fetchedData.sharedFiles[i].length);
            }

            ///////////////////////////////////////////////////

            const options2 = {
                method: "GET",
                headers: {
                    Authorization: mystring,
                },
            };

            const functionWithPromise = (item) => {
                //a function that returns a promise
                return Promise.resolve("ok");
            };

            const anAsyncFunction = async (props) => {
                var folderName = props[0];
                var dataentry = props[1];

                var keys = Object.keys(dataentry);

                for (var i in dataentry) {
                    for (var j = 0; j < dataentry[i].length; j++) {
                        var resp = await fetch(
                            `http://localhost:8080/api/fileDrop/download?file_path=${i}/${dataentry[i][j]}`,
                            options2
                        );
                        sizes[i][j] = resp.headers.get("content-length") / 1000;
                        locations[i][j] = "[0.0, 0.0]";
                    }
                }
                return functionWithPromise(dataentry);
            };

            const promises = [];
            Object.entries(fetchedData).map((dataentry) => {
                promises.push(anAsyncFunction(dataentry));
            });
            await Promise.all(promises).then(() => {
                console.log(sizes);
            });

            var arr = [];
            var folders = [];
            await fetch("http://localhost:8080/api/fileDrop", options)
                .then((response) => response.json())
                .then((outerdata) => {
                    Object.entries(outerdata).map((dat) => {
                        var data = dat[1];
                        console.log("DATA=");
                        console.log(data);
                        for (var i in data) {
                            if (i === username)
                                // if this is main directory
                                for (var j = 0; j < data[i].length; j++) {
                                    console.log("i=" + i + ", j=" + j);
                                    console.log(sizes[i][j]);
                                    checkSubfolder(
                                        arr,
                                        data[i][j],
                                        sizes[i][j],
                                        locations[i][j],
                                        data[i][j],
                                        i
                                    );
                                }
                            else if (i.startsWith(username)) {
                                var folderName = i.substr(
                                    username.length + 1,
                                    i.length - 1
                                );
                                // if this is a subdirectory
                                var lArr = [];
                                if (!folders.includes(folderName))
                                    lArr.push(
                                        fileDesc(
                                            folderName,
                                            0,
                                            "",
                                            "",
                                            folderName,
                                            "dir",
                                            "dir"
                                        )
                                    );
                                for (var j = 0; j < data[i].length; j++) {
                                    var folderLocation =
                                        username + "/" + folderName;
                                    if (i.startsWith(folderLocation))
                                        checkSubfolder(
                                            lArr,
                                            data[i][j],
                                            sizes[i][j],
                                            locations[i][j],
                                            folderName + "/" + data[i][j],
                                            i
                                        );
                                    folders.push(folderLocation);
                                }
                                arr.push(lArr);
                            } else {
                                console.log(
                                    "##################################################"
                                );
                                console.log("SHARED FILES:");
                                // if this file is shared

                                // for (var j = 0; j < data[i].length; j++) {
                                //     console.log("i=" + i + ", j=" + j);
                                //     console.log(`data[i][j]`, data[i][j]);
                                //     console.log(`sizes[i][j]`, sizes[i][j]);
                                //     checkSubfolder(
                                //         arr,
                                //         data[i][j],
                                //         sizes[i][j],
                                //         locations[i][j],
                                //         data[i][j],
                                //         i
                                //     );
                                // }

                                var folderName = i;
                                // if this is a subdirectory
                                var lArr = [];
                                if (!folders.includes(folderName))
                                    lArr.push(
                                        fileDesc(
                                            folderName,
                                            0,
                                            "",
                                            "",
                                            folderName,
                                            "dir",
                                            "dir"
                                        )
                                    );
                                for (var j = 0; j < data[i].length; j++) {
                                    var folderLocation =
                                        username + "/" + folderName;
                                    console.log(
                                        `folderLocation`,
                                        folderLocation
                                    );

                                    checkSubfolder(
                                        lArr,
                                        data[i][j],
                                        sizes[i][j],
                                        locations[i][j],
                                        folderName + "/" + data[i][j],
                                        i
                                    );
                                    folders.push(folderLocation);
                                }
                                arr.push(lArr);

                                console.log(
                                    "##################################################"
                                );
                            }
                        }
                    });
                });

            setFiles(arr);
            setBackup(arr);
        } else alert("Wrong username or password");
    };

    const deleteAccount = async () => {
        const requestOptions = {
            method: "GET",
            headers: {
                Accept: "application/json",
                "Content-Type": "application/json",
                Authorization: token,
                // credentials: "include",
            },
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
        var mytoken;

        setToken("");

        setRedirect(false);
        setLoggedin(false);

        setFiles([]);
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
            setUsername(username);

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
                //credentials: "include",
            };

            var mytoken;
            await fetch("http://localhost:8080/api/auth/signIn", requestOptions)
                .then((response) => response.json())
                .then((data) => {
                    mytoken = data.token;
                });

            const rawResponse = await fetch(
                "http://localhost:8080/api/auth/signIn",
                requestOptions
            );

            // If login was successful.
            if (rawResponse.status === 200) {
                setRedirect(true);
                setLoggedin(true);
                setUsername(username);

                var mystring = "Bearer " + mytoken;
                setToken(mystring);

                const options = {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                        Accept: "application/json",
                        Authorization: mystring,
                    },
                };

                /////////////////////////////////////////////////////////////////////////////////////////////////////////////
                // Initial fetch of the data to iterate over and get file sizes.
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////

                var fetchedData;

                await fetch("http://localhost:8080/api/fileDrop", options)
                    .then((response) => response.json())
                    .then((outerdata) => {
                        fetchedData = outerdata;
                    });

                // get array size///////////////////////////////

                var k = 0;
                // ile jest lokalizacji moich i wspoldzielonych?
                for (var i in fetchedData.myFiles) k++;

                for (var i in fetchedData.sharedFiles) k++;

                // utworz tablice z iloscia elementow rowna ilosci lokalizacji
                var sizes = new Array(k);
                var locations = new Array(k);

                // dla kazdej z tych lokalizacji utworz tablice o rozmiarze tyle ile jest w niej plikow
                for (var i in fetchedData.myFiles) {
                    sizes[i] = new Array(fetchedData.myFiles[i].length);
                    locations[i] = new Array(fetchedData.myFiles[i].length);
                }
                for (var i in fetchedData.sharedFiles) {
                    sizes[i] = new Array(fetchedData.sharedFiles[i].length);
                    locations[i] = new Array(fetchedData.sharedFiles[i].length);
                }

                ///////////////////////////////////////////////////

                const options2 = {
                    method: "GET",
                    headers: {
                        Authorization: mystring,
                    },
                };

                const functionWithPromise = (item) => {
                    //a function that returns a promise
                    return Promise.resolve("ok");
                };

                const anAsyncFunction = async (props) => {
                    var folderName = props[0];
                    var dataentry = props[1];

                    var keys = Object.keys(dataentry);

                    for (var i in dataentry) {
                        for (var j = 0; j < dataentry[i].length; j++) {
                            var resp = await fetch(
                                `http://localhost:8080/api/fileDrop/download?file_path=${i}/${dataentry[i][j]}`,
                                options2
                            );
                            sizes[i][j] =
                                resp.headers.get("content-length") / 1000;
                            locations[i][j] = "[0.0, 0.0]";
                        }
                    }
                    return functionWithPromise(dataentry);
                };

                const promises = [];
                Object.entries(fetchedData).map((dataentry) => {
                    promises.push(anAsyncFunction(dataentry));
                });
                await Promise.all(promises).then(() => {
                    console.log(sizes);
                });

                var arr = [];
                var folders = [];
                await fetch("http://localhost:8080/api/fileDrop", options)
                    .then((response) => response.json())
                    .then((outerdata) => {
                        Object.entries(outerdata).map((dat) => {
                            var data = dat[1];
                            //console.log(data);
                            for (var i in data) {
                                if (i === username)
                                    // if this is main directory
                                    for (var j = 0; j < data[i].length; j++) {
                                        console.log("i=" + i + ", j=" + j);
                                        console.log(sizes[i][j]);
                                        checkSubfolder(
                                            arr,
                                            data[i][j],
                                            sizes[i][j],
                                            locations[i][j],
                                            data[i][j],
                                            i
                                        );
                                    }
                                else if (i.startsWith(username)) {
                                    var folderName = i.substr(
                                        username.length + 1,
                                        i.length - 1
                                    );
                                    // if this is a subdirectory
                                    var lArr = [];
                                    if (!folders.includes(folderName))
                                        lArr.push(
                                            fileDesc(
                                                folderName,
                                                0,
                                                "",
                                                "",
                                                folderName,
                                                "dir",
                                                "dir"
                                            )
                                        );
                                    for (var j = 0; j < data[i].length; j++) {
                                        var folderLocation =
                                            username + "/" + folderName;
                                        if (i.startsWith(folderLocation))
                                            checkSubfolder(
                                                lArr,
                                                data[i][j],
                                                sizes[i][j],
                                                locations[i][j],
                                                folderName + "/" + data[i][j],
                                                i
                                            );
                                        folders.push(folderLocation);
                                    }
                                    arr.push(lArr);
                                } else {
                                    // if this file is shared
                                    for (var j = 0; j < data[i].length; j++) {
                                        console.log("i=" + i + ", j=" + j);
                                        console.log(sizes[i][j]);
                                        checkSubfolder(
                                            arr,
                                            data[i][j],
                                            sizes[i][j],
                                            locations[i][j],
                                            data[i][j],
                                            i
                                        );
                                    }
                                }
                            }
                        });
                    });

                setFiles(arr);
                setBackup(arr);
            } else alert("Wrong username or password");
        } else alert("Wrong username, email, password, or dob");
    };

    const onFolder = (file2) => {
        console.log("before folder");
        console.log(files);
        setBackup(_.cloneDeep(files));
        setCurrFolder(file2[0].name);
        file2[0].name = "..";
        file2[0].owner = username;
        setFiles(file2);
        console.log("after folder");
        console.log(files);
        onGoBack();
    };

    const onRestore = () => {
        console.log("before restore");
        console.log(files);
        setFiles(backup);
        console.log("after restore");
        console.log(files);
        onGoBack();
    };

    const onDelete = async (id) => {
        // Delete from server
        const found = files.find((x) => x.id == id);
        console.log(`${username}/${found.address}`);
        const options = {
            method: "DELETE",
            headers: {
                Authorization: token,
            },
        };
        await fetch(
            `http://localhost:8080/api/fileDrop/delete?file_path=${username}/${found.address}`,
            options
        );

        // Delete from UI
        refresh();
    };

    const onShare = async (id, input) => {
        console.log(id);
        console.log(input);
        const curFile = files.find((element) => element.id === id);
        const data = new FormData();
        console.log(curFile.address);
        data.append("file_path", username + "/" + curFile.address);
        data.append("email", input);
        axios
            .post(`http://localhost:8080/api/fileDrop/share`, data, {
                headers: { Authorization: token },
            })
            .then((res) => {
                refresh();
                console.log(res.statusText);
            });
        setFiles([...files]);
        onGoBack();
        setDummy((dummy) => dummy + 1);
    };
    //const history = useHistory();

    const onSortAlphaDown = () => {
        setSortBy("name");
        setAscDesc("asc");
        console.log(sortBy, ascDesc);
        setDummy((dummy) => dummy + 1);
        //return <Redirect to="/help" />;
    };
    const onSortAlphaDownAlt = () => {
        setSortBy("name");
        setAscDesc("desc");
        console.log(sortBy, ascDesc);
        setDummy((dummy) => dummy + 1);
        //return <Redirect to="/help" />;
    };
    const onSortAmountDown = () => {
        setSortBy("size");
        setAscDesc("asc");
        console.log(sortBy, ascDesc);
        setDummy((dummy) => dummy + 1);
        //return <Redirect to="/help" />;
    };
    const onSortAmountDownAlt = () => {
        setSortBy("size");
        setAscDesc("desc");
        console.log(sortBy, ascDesc);
        console.log("hey");
        setDummy((dummy) => dummy + 1);
        //return <Redirect to="/help" />;
    };

    const onOpen = (id) => {
        files.find((file, index, array) => {
            // If we found the file we clicked, and we are in a directory...
            if (array[0].name == ".." && file.id === id) {
                setFile(currFolder + "/" + file.name);
            } else if (file.id === id) {
                setFile(file.name);
            }
        });
        const found = files.find((file, index, array) => file.id === id);
        console.log(found.owner);
        setFileOwner(found.owner);
        setId(id);
    };

    const onRename = (value, id) => {
        for (var i in files) {
            if (files[i].id === id) {
                console.log(
                    `username + "/" + files[i].name`,
                    username + "/" + files[i].name
                );
                const data = new FormData();
                data.append("", "");
                console.log(`username`, username);
                console.log(`files[i].name`, files[i].name);
                axios
                    .post(
                        `http://localhost:8080/api/fileDrop/rename?file_path=${
                            username + "/" + files[i].name
                        }&name=${value}`,
                        data,
                        {
                            headers: { Authorization: token },
                        }
                    )
                    .catch((error) => {
                        console.log(`error`, error);
                    });

                files[i].name = value;

                // console.log(`value`, value);
                // console.log(
                //     `username + "/" + file.name`,
                //     username + "/" + file[i].name
                // );
                // console.log(`token`, token);

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
        const data = new FormData();
        data.append("files", file.file);
        data.append("dir", file.dir);

        axios
            .post("http://localhost:8080/api/fileDrop", data, {
                headers: { Authorization: token },
            })
            .then((res) => {
                refresh();
                console.log(res.statusText);
            });
        onGoBack();
    };

    const onNight = () => {
        document.body.style.backgroundColor === "white"
            ? (document.body.style.backgroundColor = "black")
            : (document.body.style.backgroundColor = "white");

        document.body.style.color === "black"
            ? (document.body.style.color = "white")
            : (document.body.style.color = "black");

        var elements = document.getElementsByClassName("fa");
        for (var i = 0; i < elements.length; i++) {
            document.body.style.backgroundColor === "black"
                ? (elements.item(i).style.color = "white")
                : (elements.item(i).style.color = "black");
        }

        var elements = document.getElementsByClassName("logo");
        for (var i = 0; i < elements.length; i++) {
            document.body.style.backgroundColor === "black"
                ? (elements.item(i).style.filter = "invert(100%)")
                : (elements.item(i).style.filter = "invert(0%)");
        }
    };

    //
    // Helper functions
    //

    const fileDesc = (name, size, location, owner, address, type, mytype) => {
        return {
            id: nextId().slice(2, 20),
            name: name,
            size: size,
            location: location,
            owner: owner,
            address: address,
            type: type,
            mytype: mytype,
        };
    };

    const checkSubfolder = (arr, name, size, location, address, owner) => {
        if (name.endsWith("txt"))
            arr.push(
                fileDesc(name, size, location, owner, address, "text", "text")
            );
        else
            arr.push(
                fileDesc(name, size, location, owner, address, "img", "img")
            );
    };

    const refresh = async () => {
        const options = {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Accept: "application/json",
                Authorization: token,
            },
        };

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Initial fetch of the data to iterate over and get file sizes.
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////

        var fetchedData;

        await fetch("http://localhost:8080/api/fileDrop", options)
            .then((response) => response.json())
            .then((outerdata) => {
                fetchedData = outerdata;
            });

        // get array size///////////////////////////////

        var k = 0;
        // ile jest lokalizacji moich i wspoldzielonych?
        for (var i in fetchedData.myFiles) k++;

        for (var i in fetchedData.sharedFiles) k++;

        // utworz tablice z iloscia elementow rowna ilosci lokalizacji
        var sizes = new Array(k);
        var locations = new Array(k);

        // dla kazdej z tych lokalizacji utworz tablice o rozmiarze tyle ile jest w niej plikow
        for (var i in fetchedData.myFiles) {
            sizes[i] = new Array(fetchedData.myFiles[i].length);
            locations[i] = new Array(fetchedData.myFiles[i].length);
        }
        for (var i in fetchedData.sharedFiles) {
            sizes[i] = new Array(fetchedData.sharedFiles[i].length);
            locations[i] = new Array(fetchedData.sharedFiles[i].length);
        }

        ///////////////////////////////////////////////////

        const options2 = {
            method: "GET",
            headers: {
                Authorization: token,
            },
        };

        const functionWithPromise = (item) => {
            //a function that returns a promise
            return Promise.resolve("ok");
        };

        const anAsyncFunction = async (props) => {
            var folderName = props[0];
            var dataentry = props[1];

            // console.log(folderName);
            // console.log("equal?");

            // console.log(username);
            // if (folderName !== username) {
            //     var fn = folderName.substr(
            //         username.length + 1,
            //         folderName.length - 1
            //     );
            //     dataentry = fn + dataentry;
            // }
            //console.log(dataentry);

            var keys = Object.keys(dataentry);

            for (var i in dataentry) {
                for (var j = 0; j < dataentry[i].length; j++) {
                    // console.log(keys);
                    // console.log(i);
                    // console.log(keys[i]);
                    // console.log(`${i}/${dataentry[i][j]}`);

                    var resp = await fetch(
                        `http://localhost:8080/api/fileDrop/download?file_path=${i}/${dataentry[i][j]}`,
                        options2
                    );
                    sizes[i][j] = resp.headers.get("content-length") / 1000;
                    locations[i][j] = "[0.0, 0.0]";
                }
            }
            return functionWithPromise(dataentry);
        };

        const promises = [];
        Object.entries(fetchedData).map((dataentry) => {
            promises.push(anAsyncFunction(dataentry));
        });
        await Promise.all(promises).then(() => {
            console.log(sizes);
        });

        var arr = [];
        var folders = [];
        await fetch("http://localhost:8080/api/fileDrop", options)
            .then((response) => response.json())
            .then((outerdata) => {
                Object.entries(outerdata).map((dat) => {
                    var data = dat[1];
                    //console.log(data);
                    for (var i in data) {
                        if (i === username)
                            // if this is main directory
                            for (var j = 0; j < data[i].length; j++) {
                                console.log("i=" + i + ", j=" + j);
                                console.log(sizes[i][j]);
                                checkSubfolder(
                                    arr,
                                    data[i][j],
                                    sizes[i][j],
                                    locations[i][j],
                                    data[i][j],
                                    i
                                );
                            }
                        else if (i.startsWith(username)) {
                            var folderName = i.substr(
                                username.length + 1,
                                i.length - 1
                            );
                            // if this is a subdirectory
                            var lArr = [];
                            if (!folders.includes(folderName))
                                lArr.push(
                                    fileDesc(
                                        folderName,
                                        0,
                                        "",
                                        "",
                                        folderName,
                                        "dir",
                                        "dir"
                                    )
                                );
                            for (var j = 0; j < data[i].length; j++) {
                                var folderLocation =
                                    username + "/" + folderName;
                                if (i.startsWith(folderLocation))
                                    checkSubfolder(
                                        lArr,
                                        data[i][j],
                                        sizes[i][j],
                                        locations[i][j],
                                        folderName + "/" + data[i][j],
                                        i
                                    );
                                folders.push(folderLocation);
                            }
                            arr.push(lArr);
                        } else {
                            // if this file is shared
                            for (var j = 0; j < data[i].length; j++) {
                                console.log("i=" + i + ", j=" + j);
                                console.log(sizes[i][j]);
                                checkSubfolder(
                                    arr,
                                    data[i][j],
                                    sizes[i][j],
                                    locations[i][j],
                                    data[i][j],
                                    i
                                );
                            }
                        }
                    }
                });
            });

        setFiles(arr);
        setBackup(arr);
    };

    //
    // State
    //

    const [showAddFile, setShowAddFile] = useState(false);
    const [username, setUsername] = useState("User");
    const [redirect, setRedirect] = useState(false);
    const [loggedin, setLoggedin] = useState(false);
    const [files, setFiles] = useState([]);
    const [temp, setTemp] = useState([]);
    const [sortBy, setSortBy] = useState("name");
    const [ascDesc, setAscDesc] = useState("asc");
    const [id, setId] = useState("id0");
    const [backup, setBackup] = useState([]);
    const [file, setFile] = useState();
    const [currFolder, setCurrFolder] = useState("");
    const [dummy, setDummy] = useState(0);
    const [token, setToken] = useState();
    const [email, setEmail] = useState();
    const [fileOwner, setFileOwner] = useState();

    useEffect(() => {
        var elements = document.getElementsByClassName("fa");
        for (var i = 0; i < elements.length; i++) {
            document.body.style.backgroundColor === "black"
                ? (elements.item(i).style.color = "white")
                : (elements.item(i).style.color = "black");
        }

        var elements = document.getElementsByClassName("logo");
        for (var i = 0; i < elements.length; i++) {
            document.body.style.backgroundColor === "black"
                ? (elements.item(i).style.filter = "invert(100%)")
                : (elements.item(i).style.filter = "invert(0%)");
        }
    });

    //
    // Render
    //

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
                                    <div className="loggedIn">
                                        Zalogowano jako {username}
                                        <div>
                                            <button
                                                className="noDeleteButton"
                                                onClick={onLogOut}
                                            >
                                                Log out
                                            </button>
                                            <Popup
                                                open={false}
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
                                    </div>
                                ) : null}

                                <div className="mainImg">
                                    <img
                                        src={logo}
                                        alt="Could not load cloud logo."
                                        className="logo"
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
                                    className="fa"
                                    onClick={onAdd}
                                />
                                <FaSortAlphaDown
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    className="fa"
                                    onClick={onSortAlphaDown}
                                />
                                <FaSortAlphaDownAlt
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    className="fa"
                                    onClick={onSortAlphaDownAlt}
                                />
                                <FaSortAmountDown
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    className="fa"
                                    onClick={onSortAmountDown}
                                />
                                <FaSortAmountDownAlt
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    className="fa"
                                    onClick={onSortAmountDownAlt}
                                />
                                <FaMoon
                                    style={{
                                        color: "black",
                                        cursor: "pointer",
                                    }}
                                    className="fa"
                                    onClick={onNight}
                                />
                                <Link to="/help">
                                    <FaQuestion
                                        style={{
                                            color: "black",
                                            cursor: "pointer",
                                        }}
                                        className="fa"
                                        onClick={onHelp}
                                    />
                                </Link>
                            </div>
                            {showAddFile && (
                                <AddFile onAddFile={onAddFile} onAdd={onAdd} />
                            )}
                            <div className="topBar">
                                <div> Name</div> <div>Size[KB]</div>{" "}
                                <div>Coordinates</div>
                                <div>Commands</div>
                            </div>
                            <Files
                                key={dummy}
                                files={files}
                                onDelete={onDelete}
                                onShare={onShare}
                                onRename={onRename}
                                sortBy={sortBy}
                                ascDesc={ascDesc}
                                onOpen={onOpen}
                                onFolder={onFolder}
                                onRestore={onRestore}
                                username={username}
                                fileOwner={fileOwner}
                                token={token}
                                dummy={dummy}
                                onGoBack={onGoBack}
                            />
                            <div className="footer">
                                Total: {files.length} Paths
                            </div>
                        </div>
                    </>
                )}
            />
            <Route
                path="/login"
                component={() => (
                    <>
                        <Login
                            onLogin={onLogin}
                            onGoBack={onGoBack}
                            setRedirect={setRedirect}
                        />
                        {redirect ? <Redirect push to="/"></Redirect> : null}
                    </>
                )}
            />
            <Route
                path="/signup"
                component={() => (
                    <>
                        <SignUp
                            onSignUp={onSignUp}
                            setRedirect={setRedirect}
                            onGoBack={onGoBack}
                        />
                        {redirect ? <Redirect push to="/"></Redirect> : null}
                    </>
                )}
            />
            <Route
                path="/help"
                component={() => (
                    <>
                        <Help
                            onLogin={onLogin}
                            setRedirect={setRedirect}
                            onGoBack={onGoBack}
                        />
                        {/* {redirect ? <Redirect push to="/"></Redirect> : null} */}
                    </>
                )}
            />
            {
                <Route
                    path="/files/:id"
                    component={() => (
                        <Child
                            id={id}
                            file={file}
                            backup={backup}
                            username={username}
                            onGoBack={onGoBack}
                            token={token}
                            fileOwner={fileOwner}
                        ></Child>
                    )}
                ></Route>
            }
        </Router>
    );
};

export default App;
