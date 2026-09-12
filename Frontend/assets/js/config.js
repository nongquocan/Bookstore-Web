// Configuration for API endpoints
const CONFIG = {
    API_BASE_URL: 'http://localhost:8080/api',
    IMAGE_PROXY_URL: 'http://localhost:8080/api/proxy/image?url=',
    STORAGE_KEYS: {
        USER: 'bookstore_user',
        TOKEN: 'bookstore_token',
        CART: 'bookstore_cart',
        WISHLIST: 'bookstore_wishlist'
    }
};

/**
 * Chuyển URL ảnh gốc (Google Books, v.v.) thành URL qua Backend Proxy.
 * Backend sẽ fetch ảnh server-side → không bị block Referrer.
 * @param {string} url - URL ảnh gốc (có thể là http hoặc https)
 * @returns {string} - URL proxy hoặc ảnh placeholder nếu không có URL
 */
function getProxiedImageUrl(url) {
    const placeholder = (typeof CONFIG !== 'undefined' && CONFIG.API_BASE_URL)
        ? null  // dùng no-image local
        : null;

    if (!url || url.trim() === '') return null; // caller sẽ dùng no-image.png

    // Nếu đã là ảnh local thì giữ nguyên
    if (url.startsWith('./') || url.startsWith('../') || url.startsWith('/')) return url;

    // Proxy qua backend
    const proxyBase = (typeof CONFIG !== 'undefined' && CONFIG.IMAGE_PROXY_URL)
        ? CONFIG.IMAGE_PROXY_URL
        : 'http://localhost:8080/api/proxy/image?url=';

    return proxyBase + encodeURIComponent(url);
}


// Mock data for testing (remove when backend is ready)
const MOCK_DATA = {
    books: [
        {
            id: 1,
            title: 'Lập Trình Java Cơ Bản',
            author: 'Nguyễn Văn An',
            categoryId: 1,
            categoryName: 'Lập Trình',
            price: 150000,
            originalPrice: 200000,
            imageUrl: 'https://via.placeholder.com/250x350?text=Java+Basic',
            rating: 4.5,
            reviews: 128,
            description: 'Cuốn sách toàn diện về lập trình Java từ cơ bản đến nâng cao.',
            inStock: true
        },
        {
            id: 2,
            title: 'Clean Code',
            author: 'Robert C. Martin',
            categoryId: 1,
            categoryName: 'Lập Trình',
            price: 250000,
            originalPrice: 300000,
            imageUrl: 'https://via.placeholder.com/250x350?text=Clean+Code',
            rating: 4.8,
            reviews: 456,
            description: 'Hướng dẫn viết mã sạch và dễ bảo trì.',
            inStock: true
        },
        {
            id: 3,
            title: 'Design Patterns',
            author: 'Gang of Four',
            categoryId: 1,
            categoryName: 'Lập Trình',
            price: 280000,
            originalPrice: 350000,
            imageUrl: 'https://via.placeholder.com/250x350?text=Design+Patterns',
            rating: 4.6,
            reviews: 234,
            description: 'Các mẫu thiết kế phần mềm phổ biến.',
            inStock: true
        },
        {
            id: 4,
            title: 'Sapiens',
            author: 'Yuval Noah Harari',
            categoryId: 2,
            categoryName: 'Lịch Sử',
            price: 180000,
            originalPrice: 250000,
            imageUrl: 'https://via.placeholder.com/250x350?text=Sapiens',
            rating: 4.7,
            reviews: 890,
            description: 'Lịch sử loài người từ thời kỳ đá đến hiện đại.',
            inStock: true
        }
    ],
    categories: [
        { id: 1, name: 'Lập Trình' },
        { id: 2, name: 'Lịch Sử' },
        { id: 3, name: 'Khoa Học' },
        { id: 4, name: 'Tiểu Thuyết' },
        { id: 5, name: 'Tự Lực' }
    ]
};
