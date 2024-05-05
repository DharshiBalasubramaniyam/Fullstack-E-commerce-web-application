import './about.css'

function About() {
    return (
        <section class="about">
            <div className="about-image"><h3>Are you ready to live well?</h3></div>
            <div class="about-content">
                <h1>Who we are?</h1>
                <p>Welcome to PURELY, your destination for premium health and wellness products designed to nourish your body, mind, and soul. At PURELY, we believe that true wellness is a journey, and we're here to support you every step of the way.</p>
                <p>At PURELY, our mission is simple: to empower individuals to live healthier, happier lives by providing them with carefully curated products that prioritize quality, efficacy, and sustainability. We believe in the power of nature to heal and nourish, and we're dedicated to sourcing only the finest natural and organic ingredients for our products.</p>
                <p>From nutritional supplements and fitness equipment to skincare essentials and relaxation aids, [Your Website Name] offers a diverse range of products to support your wellness journey. Whether you're looking to boost your energy levels, improve your fitness routine, or simply pamper yourself with luxurious self-care products, you'll find everything you need to thrive right here.</p>
                <div class="icon-container">
                    <div class="icon">
                        <i class="fa-solid fa-truck-fast"></i>
                        <span>Island wide Delivery</span>
                    </div>

                    <div class="icon">
                        <i class="fa-regular fa-credit-card"></i>
                        <span>Secure payments</span>
                    </div>

                    <div class="icon">
                        <i class="fa-solid fa-hand-holding-dollar"></i>
                        <span>Affordable price</span>
                    </div>

                    <div class="icon">
                        <i class="fa fa-leaf"></i>
                        <span>Our green journey</span>
                    </div>
                </div>
            </div>

        </section>
    )
}

export default About