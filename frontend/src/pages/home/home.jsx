import About from "../../components/about/about";
import Categories from "../../components/categories/categories";
import Footer from "../../components/footer/footer";
import Header from "../../components/header/header";
import Hero from "../../components/home_hero/hero";

function Home() {
    return (
        <>
            <Header/>
            <Hero/>
            <Categories/>
            <About/>
            <Footer/>
        </>
    )
}

export default Home;