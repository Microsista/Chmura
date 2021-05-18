import { Link } from "react-router-dom";
import logo from "../cloud.png";
import { useState, useEffect } from "react";

const Help = ({onGoBack}) => {
    const onBack = () => {
        onGoBack();
    }

    useEffect(() => {
        var elements = document.getElementsByClassName("fa");
        for (var i = 0; i < elements.length; i++) {
            document.body.style.backgroundColor === "black" ? elements.item(i).style.color = "white" : elements.item(i).style.color = "black";
        }

        var elements = document.getElementsByClassName("logo");
        for (var i = 0; i < elements.length; i++) {
            document.body.style.backgroundColor === "black" ? elements.item(i).style.filter="invert(100%)" : elements.item(i).style.filter="invert(0%)";
        }
    })

    return (
        <div className="helpContainer">
            <div className="goBack">
                <Link to="/" onClick={onBack}>Go Back</Link>
            </div>
            <img className="logo" src={logo} alt="Could not load cloud logo." />
            <div className="title unselectable">Help</div>
            <div>
                Lorem ipsum dolor sit amet consectetur adipisicing elit.
                Deleniti doloribus dolorem fuga natus quaerat voluptas hic
                consectetur, harum dicta labore rerum corrupti officiis corporis
                eveniet nam pariatur? Itaque, repellendus delectus.
            </div>
        </div>
    );
};

export default Help;
