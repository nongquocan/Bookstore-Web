// API Helper Functions
class BookStoreAPI {
    constructor(baseUrl = CONFIG.API_BASE_URL) {
        this.baseUrl = baseUrl;
    }

    // Helper method for API calls
    async request(endpoint, method = 'GET', data = null) {
        const url = `${this.baseUrl}${endpoint}`;
        const options = {
            method,
            headers: {
                'Content-Type': 'application/json',
            }
        };

        // Add authentication token if available
        const token = localStorage.getItem(CONFIG.STORAGE_KEYS.TOKEN);
        if (token) {
            options.headers['Authorization'] = `Bearer ${token}`;
        }

        if (data && method !== 'GET') {
            options.body = JSON.stringify(data);
        }

        try {
            const response = await fetch(url, options);
            if (response.status === 401 || response.status === 403) {
                // Token hết hạn hoặc không có quyền
                console.warn("Phiên đăng nhập hết hạn hoặc lỗi quyền truy cập. Đang đăng xuất...");
                localStorage.removeItem(CONFIG.STORAGE_KEYS.TOKEN);
                localStorage.removeItem(CONFIG.STORAGE_KEYS.USER);
                // Redirect an toàn từ mọi trang
                const isInPages = window.location.pathname.includes('/pages/');
                window.location.href = isInPages ? 'login.html' : './pages/login.html';
                throw new Error('Phiên đăng nhập không hợp lệ');
            }
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    // 🔐 Auth Endpoints
    async register(userData) {
        // userData cần: email, password, confirmPassword, firstName, lastName, phone
        return this.request('/auth/register', 'POST', userData);
    }

    async login(email, password) {
        return this.request('/auth/login', 'POST', { email, password });
    }

    async sendOtp(email) {
        return this.request('/auth/send-otp', 'POST', { email });
    }

    async verifyOtp(email, otp) {
        return this.request('/auth/verify-otp', 'POST', { email, otp });
    }

    async logout() {
        localStorage.removeItem(CONFIG.STORAGE_KEYS.TOKEN);
        localStorage.removeItem(CONFIG.STORAGE_KEYS.USER);
    }

    isAuthenticated() {
        return !!localStorage.getItem(CONFIG.STORAGE_KEYS.TOKEN);
    }

    // Books Endpoints
    async getBooks(filters = {}) {
        const params = new URLSearchParams({
            page: filters.page || 1,       // Backend dùng 1-indexed
            limit: filters.size || 12,
        });

        // category: truyền slug hoặc name (String) theo BookController
        // Hoặc là truyền categoryId nếu đầu vào là số
        if (filters.category && filters.category !== 'all') {
            if (!isNaN(filters.category) && !isNaN(parseFloat(filters.category))) {
                params.append('categoryId', filters.category);
            } else {
                params.append('category', filters.category);
            }
        }
        if (filters.search) {
            params.append('search', filters.search);
        }
        if (filters.sort) {
            params.append('sort', filters.sort);
        }
        if (filters.minPrice != null) {
            params.append('minPrice', filters.minPrice);
        }
        if (filters.maxPrice != null) {
            params.append('maxPrice', filters.maxPrice);
        }
        if (filters.minRating != null) {
            params.append('minRating', filters.minRating);
        }

        return this.request(`/books?${params.toString()}`);
    }

    async getBookById(id) {
        return this.request(`/books/${id}`);
    }

    async createBook(bookData) {
        return this.request('/books', 'POST', bookData);
    }

    async updateBook(id, bookData) {
        return this.request(`/books/${id}`, 'PUT', bookData);
    }

    async deleteBook(id) {
        return this.request(`/books/${id}`, 'DELETE');
    }

    // Categories Endpoints
    async getCategories() {
        return this.request('/categories');
    }

    async getCategoryById(id) {
        return this.request(`/categories/${id}`);
    }

    async createCategory(categoryData) {
        return this.request('/categories', 'POST', categoryData);
    }

    async updateCategory(id, categoryData) {
        return this.request(`/categories/${id}`, 'PUT', categoryData);
    }

    async deleteCategory(id) {
        return this.request(`/categories/${id}`, 'DELETE');
    }

    // User Endpoints
    async getCurrentUser() {
        return this.request('/auth/me');
    }

    async getUserProfile() {
        return this.request('/users/profile');
    }

    async updateUserProfile(userData) {
        return this.request('/users/profile', 'PUT', userData);
    }

    async changePassword(oldPassword, newPassword) {
        const params = new URLSearchParams({ oldPassword, newPassword });
        return this.request(`/auth/change-password?${params.toString()}`, 'POST');
    }

    async updateUserAddress(addressData) {
        return this.request('/auth/profile/address', 'PUT', addressData);
    }

    // Orders Endpoints
    async getOrders() {
        return this.request('/orders');
    }

    async getOrderById(id) {
        return this.request(`/orders/${id}`);
    }

    async createOrder(orderData) {
        return this.request('/orders/create', 'POST', orderData);
    }

    async updateOrderStatus(id, status) {
        return this.request(`/orders/${id}/status`, 'PUT', { status });
    }

    // Reviews Endpoints
    async getReviews(bookId) {
        return this.request(`/books/${bookId}/reviews`);
    }

    async createReview(bookId, reviewData) {
        return this.request(`/books/${bookId}/reviews`, 'POST', reviewData);
    }

    async updateReview(bookId, reviewId, reviewData) {
        return this.request(`/books/${bookId}/reviews/${reviewId}`, 'PUT', reviewData);
    }

    async deleteReview(bookId, reviewId) {
        return this.request(`/books/${bookId}/reviews/${reviewId}`, 'DELETE');
    }

    // Cart Endpoints
    async getCart() {
        return this.request('/cart');
    }

    async addToCart(bookId, quantity = 1) {
        const params = new URLSearchParams({ bookId, quantity });
        return this.request(`/cart/add?${params.toString()}`, 'POST');
    }

    async updateCartItem(bookId, quantity) {
        const params = new URLSearchParams({ bookId, quantity });
        return this.request(`/cart/update?${params.toString()}`, 'PUT');
    }

    async removeFromCart(bookId) {
        const params = new URLSearchParams({ bookId });
        return this.request(`/cart/remove?${params.toString()}`, 'DELETE');
    }

    async clearCart() {
        return this.request('/cart/clear', 'DELETE');
    }

    // Wishlist Endpoints
    async getWishlist() {
        return this.request('/wishlist');
    }

    async addToWishlist(bookId) {
        const params = new URLSearchParams({ bookId });
        return this.request(`/wishlist/add?${params.toString()}`, 'POST');
    }

    async removeFromWishlist(bookId) {
        const params = new URLSearchParams({ bookId });
        return this.request(`/wishlist/remove?${params.toString()}`, 'DELETE');
    }

    // Use mock data if API is not available
    async getMockBooks(filters = {}) {
        return Promise.resolve({
            content: MOCK_DATA.books,
            totalElements: MOCK_DATA.books.length,
            totalPages: 1,
            number: 0,
            size: 12
        });
    }

    async getMockCategories() {
        return Promise.resolve(MOCK_DATA.categories);
    }

    // ─── ADMIN ENDPOINTS ────────────────────────────────────────────────
    async getAdminDashboardStats() {
        return this.request('/admin/dashboard/stats');
    }

    async getAdminStats() {
        return this.request('/books/stats/overview');
    }

    // -- Users --
    async getAllUsers(page = 0, size = 10) {
        const params = new URLSearchParams({ page, size });
        return this.request(`/admin/users?${params.toString()}`);
    }

    async deleteUserByAdmin(userId) {
        return this.request(`/admin/users/${userId}`, 'DELETE');
    }

    // -- Orders --
    async getAdminOrders(page = 0, size = 50) {
        const params = new URLSearchParams({ page, size });
        return this.request(`/admin/orders?${params.toString()}`);
    }

    async updateAdminOrderStatus(orderId, status) {
        const params = new URLSearchParams({ status });
        return this.request(`/admin/orders/${orderId}/status?${params.toString()}`, 'PUT');
    }

    // -- Reviews --
    async getAdminReviews(page = 0, size = 50) {
        const params = new URLSearchParams({ page, size });
        return this.request(`/admin/reviews?${params.toString()}`);
    }

    async getAdminReviewSummaries(page = 0, size = 50) {
        const params = new URLSearchParams({ page, size });
        return this.request(`/admin/reviews/summary?${params.toString()}`);
    }

    async deleteAdminReview(reviewId) {
        return this.request(`/admin/reviews/${reviewId}`, 'DELETE');
    }

    // -- Home Layout --
    async getHomeLayout() {
        return this.request('/home-layout');
    }

    async getAdminHomeLayout() {
        return this.request('/admin/home-layout');
    }

    async updateAdminHomeLayout(id, data) {
        return this.request(`/admin/home-layout/${id}`, 'PUT', data);
    }

    // -- Books Admin --
    async updateBookPrice(bookId, listPrice, stockQuantity) {
        return this.request(`/admin/books/${bookId}/price`, 'PUT', { listPrice, stockQuantity });
    }

    async enableFlashSale(bookId, discountPercent, startTime, endTime) {
        return this.request(`/admin/books/${bookId}/flash-sale`, 'POST', {
            discountPercent,
            startTime,
            endTime
        });
    }

    async disableFlashSale(bookId) {
        return this.request(`/admin/books/${bookId}/flash-sale`, 'DELETE');
    }

    async getAdminFlashSaleBooks() {
        return this.request('/admin/books/flash-sale');
    }

    // -- Vouchers --
    async validateVoucher(code, totalAmount) {
        return this.request('/vouchers/validate', 'POST', { code, totalAmount });
    }

    async getAdminVouchers() {
        return this.request('/vouchers');
    }

    async createAdminVoucher(data) {
        return this.request('/vouchers', 'POST', data);
    }

    async updateAdminVoucher(id, data) {
        return this.request(`/vouchers/${id}`, 'PUT', data);
    }

    async deleteAdminVoucher(id) {
        return this.request(`/vouchers/${id}`, 'DELETE');
    }
}

// Create global API instance
const api = new BookStoreAPI();