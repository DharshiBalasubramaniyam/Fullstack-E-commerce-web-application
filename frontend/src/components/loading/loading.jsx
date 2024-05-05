import FadeLoader from "react-spinners/FadeLoader";

function Loading() {
    return (
        <main className="flex-center">
            <FadeLoader
                color="#056046"
                loading={true}
                size={40}
                aria-label="Loading Spinner"
                data-testid="loader"
            />
            <br/>
            <small>Please wait...</small>
        </main>
    )
}

export default Loading;