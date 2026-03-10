/* ====================================
   HÀNG SỐ VÀ BIẾN TOÀN CỤC
   ==================================== */

// API Configuration
const API_CONFIG = {
    BASE_URL: 'http://localhost:8080/api',
    TIMEOUT: 5000,
    USE_BACKEND: true  // Toggle between backend API and local data
};

const APP = {
        products: [],
        cart: [],
        favorites: [],
        currentUser: null,

        // Lấy dữ liệu từ localStorage
        init: function() {
            this.loadProducts();
            this.loadCart();
            this.loadFavorites();
            this.setupEventListeners();
            this.updateUI();
        },

        loadProducts: async function() {
            try {
                if (API_CONFIG.USE_BACKEND) {
                    // Load từ Backend API
                    const response = await fetch(`${API_CONFIG.BASE_URL}/books?limit=100`);
                    const data = await response.json();
                    
                    if (data.success && data.data.data) {
                        // Convert backend format to frontend format
                        this.products = data.data.data.map(book => ({
                            id: book.id,
                            name: book.title,
                            author: book.author,
                            category: book.category,
                            price: book.price,
                            originalPrice: book.originalPrice,
                            image: book.imageUrl,
                            rating: book.rating,
                            reviews: book.ratingCount || 0,
                            quantity: book.quantity,
                            discount: book.discountPercent,
                            isNew: book.isNew,
                            isFeatured: book.isFeatured,
                            sku: book.sku,
                            description: book.description
                        }));
                        console.log(`✅ Loaded ${this.products.length} products from Backend API`);
                    } else {
                        throw new Error('Invalid API response');
                    }
                } else {
                    // Load từ local JSON (fallback)
                    const response = await fetch('./assets/data/products.json');
                    const data = await response.json();
                    this.products = data.products;
                    console.log(`📄 Loaded ${this.products.length} products from local JSON`);
                }
            } catch (error) {
                console.error('❌ Lỗi tải sản phẩm:', error);
                console.warn('⚠️ Fallback to local data...');
                // Fallback to local JSON
                try {
                    const response = await fetch('./assets/data/products.json');
                    const data = await response.json();
                    this.products = data.products;
                } catch (fallbackError) {
                    console.error('❌ Fallback failed:', fallbackError);
                    this.products = [];
                }
            }
        },

        loadCart: function() {
            const stored = localStorage.getItem('cart');
            this.cart = stored ? JSON.parse(stored) : [];
        },

        loadFavorites: function() {
            const stored = localStorage.getItem('favorites');
            this.favorites = stored ? JSON.parse(stored) : [];
        },

        saveCart: function() {
            localStorage.setItem('cart', JSON.stringify(this.cart));
        },

        saveFavorites: function() {
            localStorage.setItem('favorites', JSON.stringify(this.favorites));
        },

        setupEventListeners: function() {
            // Tìm kiếm
            const searchInput = document.querySelector('.search-input');
            if (searchInput) {
                searchInput.addEventListener('input', this.handleSearch.bind(this));
                searchInput.addEventListener('focus', this.showSearchSuggestions.bind(this));
                searchInput.addEventListener('blur', () => {
                    setTimeout(() => {
                        const suggestions = document.querySelector('.search-suggestions');
                        if (suggestions) suggestions.classList.remove('active');
                    }, 200);
                });
            }

            // Carousel
            const carouselContainer = document.querySelector('.carousel-container');
            if (carouselContainer) {
                this.setupCarousel();
            }

            // Flash sale countdown
            const flashSaleSection = document.querySelector('.flash-sale');
            if (flashSaleSection) {
                this.startCountdown();
            }

            // Menu
            const categoryMenu = document.querySelector('[data-toggle="mega-menu"]');
            if (categoryMenu) {
                categoryMenu.addEventListener('mouseenter', () => {
                    const menu = document.querySelector('.mega-menu');
                    if (menu) menu.classList.add('active');
                });

                categoryMenu.addEventListener('mouseleave', () => {
                    const menu = document.querySelector('.mega-menu');
                    if (menu) menu.classList.remove('active');
                });
            }

            // Add to cart
            document.addEventListener('click', (e) => {
                if (e.target.closest('.btn-cart')) {
                    const productId = parseInt(e.target.closest('.btn-cart').dataset.productId);
                    this.addToCart(productId, 1);
                }

                // Wishlist
                if (e.target.closest('.btn-wishlist')) {
                    const productId = parseInt(e.target.closest('.btn-wishlist').dataset.productId);
                    this.toggleFavorite(productId);
                    e.target.closest('.btn-wishlist').classList.toggle('active');
                }
            });
        },

        // Xử lý tìm kiếm
        handleSearch: function(e) {
            const query = e.target.value.toLowerCase();

            if (query.length < 2) {
                document.querySelector('.search-suggestions').classList.remove('active');
                return;
            }

            const results = this.products.filter(product =>
                product.name.toLowerCase().includes(query) ||
                product.author.toLowerCase().includes(query) ||
                product.category.toLowerCase().includes(query)
            ).slice(0, 8);

            this.displaySearchSuggestions(results);
        },

        displaySearchSuggestions: function(results) {
            const suggestionsContainer = document.querySelector('.search-suggestions');

            if (results.length === 0) {
                suggestionsContainer.innerHTML = '<div style="padding: 16px; text-align: center; color: #666;">Không tìm thấy kết quả</div>';
                suggestionsContainer.classList.add('active');
                return;
            }

            suggestionsContainer.innerHTML = results.map(product => `
      <div class="search-suggestion-item" onclick="window.location.href='./pages/product-detail.html?id=${product.id}'">
        <div style="font-weight: 600; margin-bottom: 4px;">${product.name}</div>
        <div style="font-size: 12px; color: #999;">${product.author}</div>
      </div>
    `).join('');

            suggestionsContainer.classList.add('active');
        },

        showSearchSuggestions: function() {
            const input = document.querySelector('.search-input');
            if (input && input.value.length >= 2) {
                this.handleSearch({ target: input });
            }
        },

        // Carousel
        setupCarousel: function() {
            let currentSlide = 0;
            const slides = document.querySelectorAll('.carousel-item');
            const dots = document.querySelectorAll('.carousel-dot');
            const prevBtn = document.querySelector('.carousel-nav.prev');
            const nextBtn = document.querySelector('.carousel-nav.next');

            if (slides.length === 0) return;

            const updateCarousel = () => {
                const offset = -currentSlide * 100;
                document.querySelector('.carousel-inner').style.transform = `translateX(${offset}%)`;

                dots.forEach((dot, index) => {
                    dot.classList.toggle('active', index === currentSlide);
                });
            };

            const nextSlide = () => {
                currentSlide = (currentSlide + 1) % slides.length;
                updateCarousel();
            };

            const prevSlide = () => {
                currentSlide = (currentSlide - 1 + slides.length) % slides.length;
                updateCarousel();
            };

            if (nextBtn) nextBtn.addEventListener('click', nextSlide);
            if (prevBtn) prevBtn.addEventListener('click', prevSlide);

            dots.forEach((dot, index) => {
                dot.addEventListener('click', () => {
                    currentSlide = index;
                    updateCarousel();
                });
            });

            // Auto play
            setInterval(nextSlide, 6000);
            updateCarousel();
        },

        // Countdown Flash Sale
        startCountdown: function() {
            const countdownElements = document.querySelectorAll('[data-countdown]');

            const updateCountdown = () => {
                const futureTime = new Date().getTime() + (24 * 60 * 60 * 1000); // 24 giờ

                const interval = setInterval(() => {
                    const now = new Date().getTime();
                    const distance = futureTime - now;

                    if (distance < 0) {
                        clearInterval(interval);
                        return;
                    }

                    const days = Math.floor(distance / (1000 * 60 * 60 * 24));
                    const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
                    const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
                    const seconds = Math.floor((distance % (1000 * 60)) / 1000);

                    const timerContainer = document.querySelector('.countdown-timer');
                    if (timerContainer) {
                        timerContainer.innerHTML = `
            <div class="countdown-item">
              <span>${String(hours).padStart(2, '0')}</span>
              <span>Giờ</span>
            </div>
            <span style="font-weight: bold;">:</span>
            <div class="countdown-item">
              <span>${String(minutes).padStart(2, '0')}</span>
              <span>Phút</span>
            </div>
            <span style="font-weight: bold;">:</span>
            <div class="countdown-item">
              <span>${String(seconds).padStart(2, '0')}</span>
              <span>Giây</span>
            </div>
          `;
                    }
                }, 1000);
            };

            updateCountdown();
        },

        // Giỏ hàng
        addToCart: function(productId, quantity = 1) {
            const product = this.products.find(p => p.id === productId);
            if (!product) return;

            const existingItem = this.cart.find(item => item.id === productId);

            if (existingItem) {
                existingItem.quantity += quantity;
            } else {
                this.cart.push({
                    id: productId,
                    name: product.name,
                    price: product.price,
                    image: product.image,
                    quantity: quantity
                });
            }

            this.saveCart();
            this.updateCartUI();
            this.showNotification('Đã thêm sản phẩm vào giỏ hàng', 'success');
        },

        removeFromCart: function(productId) {
            this.cart = this.cart.filter(item => item.id !== productId);
            this.saveCart();
            this.updateCartUI();
        },

        updateCartQuantity: function(productId, quantity) {
            const item = this.cart.find(item => item.id === productId);
            if (item) {
                item.quantity = Math.max(1, quantity);
                this.saveCart();
                this.updateCartUI();
            }
        },

        getCartTotal: function() {
            return this.cart.reduce((total, item) => total + (item.price * item.quantity), 0);
        },

        getCartCount: function() {
            return this.cart.reduce((count, item) => count + item.quantity, 0);
        },

        updateCartUI: function() {
            const badge = document.querySelector('.cart-badge');
            if (badge) {
                const count = this.getCartCount();
                badge.textContent = count > 0 ? count : '';
                badge.style.display = count > 0 ? 'flex' : 'none';
            }
        },

        // Wishlist
        toggleFavorite: function(productId) {
            const index = this.favorites.indexOf(productId);
            if (index > -1) {
                this.favorites.splice(index, 1);
            } else {
                this.favorites.push(productId);
            }
            this.saveFavorites();
        },

        isFavorite: function(productId) {
            return this.favorites.includes(productId);
        },

        // Cập nhật UI
        updateUI: function() {
            this.updateCartUI();
            this.updateFavoritesUI();
        },

        updateFavoritesUI: function() {
            document.querySelectorAll('.btn-wishlist').forEach(btn => {
                const productId = parseInt(btn.dataset.productId);
                btn.classList.toggle('active', this.isFavorite(productId));
            });
        },

        // Thông báo
        showNotification: function(message, type = 'info') {
            const container = document.querySelector('.notification-container') ||
                this.createNotificationContainer();

            const notification = document.createElement('div');
            notification.className = `alert alert-${type}`;
            notification.innerHTML = `
      <i class="fas fa-${type === 'success' ? 'check-circle' : 'info-circle'}"></i>
      <span>${message}</span>
    `;

            container.appendChild(notification);

            setTimeout(() => {
                notification.style.opacity = '0';
                setTimeout(() => notification.remove(), 300);
            }, 3000);
        },

        createNotificationContainer: function() {
            const container = document.createElement('div');
            container.className = 'notification-container';
            container.style.cssText = `
      position: fixed;
      top: 20px;
      right: 20px;
      z-index: 9999;
      max-width: 400px;
    `;
            document.body.appendChild(container);
            return container;
        },

        // Định dạng tiền tệ VND
        formatPrice: function(price) {
            return new Intl.NumberFormat('vi-VN', {
                style: 'currency',
                currency: 'VND',
                minimumFractionDigits: 0
            }).format(price);
        },

        // Render sản phẩm
        renderProducts: function(products, containerId) {
                const container = document.getElementById(containerId);
                if (!container) return;

                container.innerHTML = products.map(product => `
      <div class="product-card" data-product-id="${product.id}">
        <div class="product-image-wrapper">
          <img src="${product.image}" alt="${product.name}" 
               class="product-image" loading="lazy">
          ${product.discount > 0 ? `
            <div class="product-badge">-${product.discount}%</div>
          ` : ''}
          ${product.isNew ? `
            <div class="product-badge new">Mới</div>
          ` : ''}
        </div>
        <div class="product-info">
          <h3 class="product-name">${product.name}</h3>
          <p class="product-author">${product.author}</p>
          
          <div class="product-price-group">
            <span class="product-price">${this.formatPrice(product.price)}</span>
            ${product.originalPrice > product.price ? `
              <span class="product-original-price">${this.formatPrice(product.originalPrice)}</span>
            ` : ''}
          </div>
          
          <div class="product-rating">
            <div class="product-rating-stars">
              ${'<i class="fas fa-star"></i>'.repeat(Math.floor(product.rating))}
            </div>
            <span class="product-rating-text">${product.rating} (${product.reviews})</span>
          </div>
          
          <div class="product-actions">
            <button class="btn-cart" data-product-id="${product.id}">
              <i class="fas fa-shopping-cart"></i> Thêm vào giỏ
            </button>
            <button class="btn-wishlist" data-product-id="${product.id}">
              <i class="fas fa-heart"></i>
            </button>
          </div>
        </div>
      </div>
    `).join('');
    
    this.updateFavoritesUI();
  }
};

// Khởi tạo ứng dụng khi trang tải
document.addEventListener('DOMContentLoaded', () => {
  APP.init();
});