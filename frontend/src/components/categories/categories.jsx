import './categories.css'

function Categories() {
    return (
        <section class="banner-container">

            <h1>Browse wellness by categories</h1>

            <div class="banner">
                <img src="../../public/categories/cat1.webp" alt="" />
                <div class="b-content">
                    <h3>Nutritional Supplements</h3>
                    <p>This category includes vitamins, minerals, protein powders, herbal supplements, and other dietary supplements aimed at supporting overall health and addressing specific health concerns.</p>
                    <button>Explore Now</button>
                </div>
            </div>
            <div class="banner">
                <img src="../../public/categories/cat2.jpg" alt="" />
                <div class="b-content">
                    <h3>Healthy Food and Snacks</h3>
                    <p>This category comprises organic food products, superfoods, healthy snacks, meal replacement shakes, and dietary foods tailored to various dietary preferences and lifestyles, such as gluten-free, vegan, or keto.</p>
                    <button>Explore Now</button>
                </div>
            </div>
            <div class="banner">
                <img src="../../public/categories/cat3.jpg" alt="" />
                <div class="b-content">
                    <h3>Personal Care and Beauty</h3>
                    <p>Includes skincare products, hair care items, cosmetics, bath and body products, and oral hygiene products made from natural and organic ingredients, often free from harmful chemicals.</p>
                     <button>Explore Now</button>
                </div>
            </div>
            <div class="banner">
                <img src="../../public/categories/cat4.jpg" alt="" />
                <div class="b-content">
                    <h3>Relaxation and Stress Relief</h3>
                    <p> Products designed to promote relaxation and reduce stress, such as aromatherapy diffusers, essential oils, meditation cushions, stress balls, and relaxation teas.</p>
                    <button>Explore Now</button>
                </div>
            </div>
            <div class="banner">
                <img src="../../public/categories/cat5.jpg" alt="" />
                <div class="b-content">
                    <h3>Natural Remedies and Alternative Therapies</h3>
                    <p>Herbal remedies, essential oils, homeopathic remedies, and other alternative therapies used to address common health issues and promote holistic wellness.</p>
                    <button>Explore Now</button>
                </div>
            </div>

        </section>
    )
}


export default Categories;