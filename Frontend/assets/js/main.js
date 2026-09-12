// Main application logic
class BookStore {
    constructor() {
        this.currentPage = 0;
        this.pageSize = 12;
        this.filters = {
            category: 'all',
            search: '',
            sort: 'newest'
        };
        this.cart = [];
        this.isLoggedIn = false;
        this.currentUser = null;
        this.init();
    }

    init() {
        this.loadUser();
        this.setupEventListeners();
        this.loadBooks();
        this.loadCategories();
        this.hideLoadingSpinner();
    }

    setupEventListeners() {
        // Search functionality
        document.getElementById('searchInput')?.addEventListener('input', (e) => {
            this.filters.search = e.target.value;
            this.currentPage = 0;
            this.loadBooks();
        });

        // Sort functionality
        document.getElementById('sortSelect')?.addEventListener('change', (e) => {
            this.filters.sort = e.target.value;
            this.currentPage = 0;
            this.loadBooks();
        });

        // Category filtering
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('category-btn')) {
                document.querySelectorAll('.category-btn').forEach(btn => btn.classList.remove('bg-blue-600'));
                e.target.classList.add('bg-blue-600');
                this.filters.category = e.target.dataset.category;
                this.currentPage = 0;
                this.loadBooks();
            }
        });

        // Add to cart
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('add-to-cart-btn')) {
                const bookId = e.target.dataset.bookId;
                this.addToCart(bookId);
            }
        });

        // Quick view
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('quick-view-btn')) {
                const bookId = e.target.dataset.bookId;
                this.showQuickView(bookId);
            }
        });

        // Add to wishlist
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('wishlist-btn')) {
                const bookId = e.target.dataset.bookId;
                this.toggleWishlist(bookId);
            }
        });

        // Login
        document.getElementById('loginForm')?.addEventListener('submit', (e) => {
            e.preventDefault();
            this.login();
        });

        // Logout
        document.getElementById('logoutBtn')?.addEventListener('click', () => {
            this.logout();
        });
    }

    async loadBooks() {
        this.showLoadingSpinner();
        try {
            const filters = {
                page: this.currentPage,
                size: this.pageSize
            };

            if (this.filters.search) {
                filters.search = this.filters.search;
            }

            if (this.filters.category !== 'all') {
                filters.category = this.filters.category;
            }

            // Try real API first, fall back to mock
            let data;
            try {
                data = await api.getBooks(filters);
            } catch (error) {
                console.log('Using mock data');
                data = await api.getMockBooks(filters);
            }

            this.renderBooks(data.content);
            this.renderPagination(data.totalPages);
            document.getElementById('resultsCount').textContent = `${data.totalElements} sách được tìm thấy`;
        } catch (error) {
            console.error('Error loading books:', error);
            this.showError('Lỗi tải sách. Vui lòng thử lại sau');
        } finally {
            this.hideLoadingSpinner();
        }
    }

    renderBooks(books) {
        const productsGrid = document.getElementById('productsGrid');
        productsGrid.innerHTML = '';

        books.forEach(book => {
            const discount = book.originalPrice
                ? Math.round((1 - book.price / book.originalPrice) * 100)
                : 0;

            const bookCard = document.createElement('div');
            bookCard.className = 'bg-white rounded-lg shadow-sm hover:shadow-lg transition-shadow overflow-hidden';
            bookCard.innerHTML = `
                <div class="relative">
                    <img src="${book.imageUrl}" alt="${book.title}" class="w-full h-64 object-cover">
                    ${discount > 0 ? `<div class="absolute top-2 right-2 bg-red-500 text-white px-2 py-1 rounded text-sm font-bold">-${discount}%</div>` : ''}
                    <button class="wishlist-btn absolute top-2 left-2 bg-white rounded-full w-8 h-8 flex items-center justify-center shadow" data-book-id="${book.id}">❤️</button>
                </div>
                <div class="p-4">
                    <h3 class="text-sm font-semibold text-gray-800 line-clamp-2 mb-2">${book.title}</h3>
                    <p class="text-xs text-gray-600 mb-3">${book.author}</p>
                    <div class="flex items-center mb-3">
                        <span class="text-yellow-400">★</span>
                        <span class="text-sm ml-1">${book.rating} (${book.reviews})</span>
                    </div>
                    <div class="flex items-baseline gap-2 mb-4">
                        <span class="text-lg font-bold text-blue-600">${this.formatPrice(book.price)}đ</span>
                        ${book.originalPrice ? `<span class="text-sm text-gray-400 line-through">${this.formatPrice(book.originalPrice)}đ</span>` : ''}
                    </div>
                    <button class="quick-view-btn w-full px-3 py-2 border border-blue-600 text-blue-600 rounded text-sm hover:bg-blue-50 mb-2" data-book-id="${book.id}">Xem Chi Tiết</button>
                    <button class="add-to-cart-btn w-full px-3 py-2 bg-blue-600 text-white rounded text-sm hover:bg-blue-700" data-book-id="${book.id}">
                        🛒 Thêm Vào Giỏ
                    </button>
                </div>
            `;
            productsGrid.appendChild(bookCard);
        });
    }

    renderPagination(totalPages) {
        const container = document.getElementById('paginationContainer');
        container.innerHTML = '';

        if (totalPages <= 1) return;

        // Previous button
        if (this.currentPage > 0) {
            const prevBtn = document.createElement('button');
            prevBtn.textContent = '← Trước';
            prevBtn.className = 'px-4 py-2 border border-gray-300 rounded hover:bg-gray-100';
            prevBtn.addEventListener('click', () => {
                this.currentPage--;
                this.loadBooks();
                window.scrollTo(0, 0);
            });
            container.appendChild(prevBtn);
        }

        // Page numbers
        for (let i = Math.max(0, this.currentPage - 2); i < Math.min(totalPages, this.currentPage + 3); i++) {
            const btn = document.createElement('button');
            btn.textContent = i + 1;
            btn.className = `px-4 py-2 border rounded ${
                i === this.currentPage
                    ? 'bg-blue-600 text-white border-blue-600'
                    : 'border-gray-300 hover:bg-gray-100'
            }`;
            btn.addEventListener('click', () => {
                this.currentPage = i;
                this.loadBooks();
                window.scrollTo(0, 0);
            });
            container.appendChild(btn);
        }

        // Next button
        if (this.currentPage < totalPages - 1) {
            const nextBtn = document.createElement('button');
            nextBtn.textContent = 'Sau →';
            nextBtn.className = 'px-4 py-2 border border-gray-300 rounded hover:bg-gray-100';
            nextBtn.addEventListener('click', () => {
                this.currentPage++;
                this.loadBooks();
                window.scrollTo(0, 0);
            });
            container.appendChild(nextBtn);
        }
    }

    async loadCategories() {
        try {
            let categories;
            try {
                categories = await api.getCategories();
            } catch (error) {
                categories = await api.getMockCategories();
            }

            const categoryList = document.getElementById('categoryList');
            categoryList.innerHTML = '';

            categories.forEach(category => {
                const btn = document.createElement('button');
                btn.className = 'category-btn px-4 py-2 rounded-lg border border-gray-300 text-gray-700 hover:bg-blue-50 whitespace-nowrap';
                btn.textContent = category.name;
                btn.dataset.category = category.slug || category.name; // dùng slug khớp BookController @RequestParam String category
                categoryList.appendChild(btn);
            });
        } catch (error) {
            console.error('Error loading categories:', error);
        }
    }

    async addToCart(bookId, quantity = 1) {
        if (!api.isAuthenticated()) {
            this.showNotification('Vui lòng đăng nhập để thêm vào giỏ hàng');
            return;
        }
        try {
            await api.addToCart(bookId, quantity);
            this.showNotification('Đã thêm vào giỏ hàng');
            await this.updateCartCountFromBackend();
        } catch (error) {
            console.error('Error adding to cart:', error);
            this.showNotification('Không thể thêm vào giỏ hàng');
        }
    }

    async updateCartCountFromBackend() {
        if (!api.isAuthenticated()) {
            this.setCartCount(0);
            return;
        }
        try {
            const response = await api.getCart();
            const cart = response.data || [];
            this.setCartCount(cart.length);
        } catch (error) {
            this.setCartCount(0);
        }
    }

    setCartCount(count) {
        // Update all cart badge elements
        const badges = document.querySelectorAll('.cart-badge');
        badges.forEach(badge => {
            badge.textContent = count;
            badge.style.display = count > 0 ? 'inline-block' : 'none';
        });
    }

    async toggleWishlist(bookId) {
        if (!api.isAuthenticated()) {
            this.showNotification('Vui lòng đăng nhập để thêm vào danh sách yêu thích');
            return;
        }

        try {
            const wishlist = await api.getWishlist();
            const items = wishlist.data || [];
            const existing = items.find(item => item.bookId === bookId);

            if (existing) {
                await api.removeFromWishlist(bookId);
                this.showNotification('Đã xóa khỏi danh sách yêu thích');
            } else {
                await api.addToWishlist(bookId);
                this.showNotification('Đã thêm vào danh sách yêu thích');
            }
        } catch (error) {
            console.error('Error toggling wishlist:', error);
            this.showNotification('Không thể cập nhật danh sách yêu thích');
        }
    }

    async loadUser() {
        const user = localStorage.getItem(CONFIG.STORAGE_KEYS.USER);
        if (user) {
            this.currentUser = JSON.parse(user);
            this.isLoggedIn = true;
            this.updateUserUI();
        } else {
            this.isLoggedIn = false;
            this.updateUserUI();
        }
    }

    async login() {
        const email = document.getElementById('emailInput').value;
        const password = document.getElementById('passwordInput').value;

        try {
            const response = await api.login(email, password);
            localStorage.setItem(CONFIG.STORAGE_KEYS.TOKEN, response.token);
            localStorage.setItem(CONFIG.STORAGE_KEYS.USER, JSON.stringify(response.user));
            this.isLoggedIn = true;
            this.currentUser = response.user;
            this.updateUserUI();
            document.getElementById('loginModal').classList.add('hidden');
            this.showNotification('Đăng nhập thành công');
        } catch (error) {
            this.showError('Email hoặc mật khẩu không chính xác');
        }
    }

    logout() {
        localStorage.removeItem(CONFIG.STORAGE_KEYS.TOKEN);
        localStorage.removeItem(CONFIG.STORAGE_KEYS.USER);
        this.isLoggedIn = false;
        this.currentUser = null;
        this.updateUserUI();
        this.showNotification('Đã đăng xuất');
    }

    updateUserUI() {
        const loginBtn = document.getElementById('btnLogin');
        const userMenu = document.getElementById('userMenu');

        if (this.isLoggedIn && this.currentUser) {
            if (loginBtn) loginBtn.style.display = 'none';
            if (userMenu) {
                userMenu.style.display = 'block';
                const initial = (this.currentUser.firstName || this.currentUser.name || 'U').charAt(0).toUpperCase();
                const avatar = userMenu.querySelector('img');
                if (avatar) avatar.src = `https://via.placeholder.com/32x32?text=${initial}`;
            }
        } else {
            if (loginBtn) loginBtn.style.display = 'flex';
            if (userMenu) userMenu.style.display = 'none';
        }
    }

    showQuickView(bookId) {
        // Quick view modal implementation
        console.log('Quick view for book:', bookId);
    }


    formatPrice(price) {
        return new Intl.NumberFormat('vi-VN').format(price);
    }

    showNotification(message) {
        const notification = document.createElement('div');
        notification.className = 'fixed bottom-4 right-4 bg-green-500 text-white px-6 py-3 rounded-lg shadow-lg z-50';
        notification.textContent = message;
        document.body.appendChild(notification);
        setTimeout(() => notification.remove(), 3000);
    }

    showError(message) {
        const notification = document.createElement('div');
        notification.className = 'fixed bottom-4 right-4 bg-red-500 text-white px-6 py-3 rounded-lg shadow-lg z-50';
        notification.textContent = message;
        document.body.appendChild(notification);
        setTimeout(() => notification.remove(), 3000);
    }

    showLoadingSpinner() {
        document.getElementById('loadingSpinner').classList.remove('hidden');
    }

    hideLoadingSpinner() {
        document.getElementById('loadingSpinner').classList.add('hidden');
    }
}

// Initialize app when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    window.bookStore = new BookStore();
});