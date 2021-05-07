// import { useParams } from "react";
import { Link } from "react-router-dom";

const Child = ({ id, files }) => {
    // let { id } = useParams();
    return (
        <div>
            <div>
                <Link to="/">Go Back</Link>
            </div>
            <div>Name: {files[id - 1].name}</div>
            {files[id - 1].type == "image" ? (
                <img src={files[id - 1].address} />
            ) : (
                files[id - 1]
            )}
        </div>
    );
};

export default Child;
