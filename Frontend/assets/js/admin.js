// Admin Dashboard Logic
class AdminDashboard {
    constructor() {
        this.currentPage = 'dashboard';
        this.booksState = {
            page: 1,
            size: 20,
            hasMore: true,
            loading: false,
            search: '',
            category: ''
        };
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.loadDashboard();
        this.checkAdminAuth();
    }

    checkAdminAuth() {
        // Kiểm tra quyền admin — hỗ trợ nhiều format backend trả về
        const user = JSON.parse(localStorage.getItem(CONFIG.STORAGE_KEYS.USER) || 'null');
        if (!user) {
            window.location.href = '../pages/login.html';
            return;
        }

        const roles = user.roles || user.authorities || [];
        // Hỗ trợ: ['ROLE_ADMIN'], ['ADMIN'], [{name:'ROLE_ADMIN'}], [{authority:'ROLE_ADMIN'}]
        const isAdmin = roles.some(function(r) {
            if (typeof r === 'string') return r === 'ROLE_ADMIN' || r === 'ADMIN';
            var val = r.name || r.authority || r.role || '';
            return val === 'ROLE_ADMIN' || val === 'ADMIN';
        });

        if (!isAdmin) {
            console.warn('[AdminJS] Không có quyền admin, roles:', JSON.stringify(roles));
            window.location.href = '../pages/login.html';
        }
    }

    setupEventListeners() {
        // Navigation
        document.querySelectorAll('.admin-nav-link').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const page = e.currentTarget.dataset.page;
                this.switchPage(page);
            });
        });

        // Add Book Button
        document.getElementById('addBookBtn')?.addEventListener('click', () => {
            this.showBookModal(null);
        });

        // Add Category Button
        document.getElementById('addCategoryBtn')?.addEventListener('click', () => {
            this.showCategoryModal();
        });

        // Book Form Submit
        document.getElementById('bookForm')?.addEventListener('submit', (e) => {
            e.preventDefault();
            this.saveBook();
        });

        // Category Form Submit
        document.getElementById('categoryForm')?.addEventListener('submit', (e) => {
            e.preventDefault();
            this.saveCategory();
        });

        // Layout Form Submit
        document.getElementById('layoutForm')?.addEventListener('submit', (e) => {
            e.preventDefault();
            this.saveHomeLayout();
        });

        // Books Filter & Search
        const bookSearch = document.getElementById('bookSearch');
        if (bookSearch) {
            let searchTimeout;
            bookSearch.addEventListener('input', (e) => {
                clearTimeout(searchTimeout);
                searchTimeout = setTimeout(() => {
                    if (this.booksState) {
                        this.booksState.search = e.target.value.trim();
                        this.loadBooks(false);
                    }
                }, 500);
            });
        }

        const categoryFilter = document.getElementById('categoryFilter');
        if (categoryFilter) {
            categoryFilter.addEventListener('change', (e) => {
                if (this.booksState) {
                    this.booksState.category = e.target.value;
                    this.loadBooks(false);
                }
            });
        }

        // Infinite Scroll on table-wrapper
        document.querySelectorAll('.table-wrapper').forEach(wrapper => {
            wrapper.addEventListener('scroll', (e) => {
                const target = e.currentTarget;
                if (target.scrollTop + target.clientHeight >= target.scrollHeight - 50) {
                    if (this.currentPage === 'books' && this.booksState && !this.booksState.loading && this.booksState.hasMore) {
                        this.loadBooks(true);
                    } else if (this.currentPage === 'orders' && this.ordersState && !this.ordersState.loading && this.ordersState.hasMore) {
                        this.loadOrders(true);
                    } else if (this.currentPage === 'users' && this.usersState && !this.usersState.loading && this.usersState.hasMore) {
                        this.loadUsers(true);
                    } else if (this.currentPage === 'reviews' && this.reviewsState && !this.reviewsState.loading && this.reviewsState.hasMore) {
                        this.loadReviews(true);
                    }
                }
            });
        });
    }

    switchPage(page) {
        // Hide all pages
        document.querySelectorAll('.page-content').forEach(p => p.classList.add('hidden'));
        
        // Show selected page
        const selectedPage = document.getElementById(page);
        if (selectedPage) {
            selectedPage.classList.remove('hidden');
        }

        // Update active nav link
        document.querySelectorAll('.admin-nav-link').forEach(link => {
            link.classList.remove('active');
        });
        const activeNavLink = document.querySelector(`[data-page="${page}"]`);
        if (activeNavLink) activeNavLink.classList.add('active');

        // Update page title
        const titles = {
            dashboard: 'Dashboard',
            books: 'Quản Lý Sách',
            'flash-sale': 'Quản Lý Flash Sale',
            categories: 'Quản Lý Danh Mục',
            orders: 'Quản Lý Đơn Hàng',
            users: 'Quản Lý Người Dùng',
            reviews: 'Quản Lý Đánh Giá',
            settings: 'Cài Đặt'
        };
        document.getElementById('pageTitle').textContent = titles[page] || 'Dashboard';

        // Load page data
        this.loadPageData(page);

        this.currentPage = page;
    }

    async loadPageData(page) {
        switch(page) {
            case 'dashboard':
                await this.loadDashboard();
                break;
            case 'books':
                await this.loadBooks(false);
                break;
            case 'flash-sale':
                await this.loadFlashSaleManagement();
                break;
            case 'categories':
                await this.loadCategories();
                break;
            case 'orders':
                await this.loadOrders(false);
                break;
            case 'users':
                await this.loadUsers(false);
                break;
            case 'reviews':
                await this.loadReviews(false);
                break;
            case 'layout':
                await this.loadHomeLayout();
                break;
        }
    }

    async loadHomeLayout() {
        try {
            const tbody = document.getElementById('layoutTableBody');
            tbody.innerHTML = '<tr><td colspan="6" class="text-center py-8">Đang tải cấu hình layout...</td></tr>';

            const res = await api.getAdminHomeLayout();
            const layouts = res.data || [];

            if (layouts.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center py-8 text-gray-500">Chưa có cấu hình layout nào</td></tr>';
            } else {
                tbody.innerHTML = layouts.map(item => `
                    <tr>
                        <td class="text-center font-bold">${item.displayOrder}</td>
                        <td class="font-semibold">${item.title}</td>
                        <td><span class="badge badge-secondary">${item.sectionKey}</span></td>
                        <td class="text-center">${item.itemCount}</td>
                        <td>
                            <span class="badge ${item.active ? 'badge-success' : 'badge-danger'}">
                                ${item.active ? 'Hiển thị' : 'Ẩn'}
                            </span>
                        </td>
                        <td>
                            <button onclick="editLayout(${item.id})" class="text-blue-600 hover:underline font-semibold">
                                Sửa
                            </button>
                        </td>
                    </tr>
                `).join('');
            }
        } catch (error) {
            console.error('Error loading layout:', error);
            document.getElementById('layoutTableBody').innerHTML = '<tr><td colspan="6" class="text-center py-8 text-red-500">Lỗi tải dữ liệu layout</td></tr>';
        }
    }

    async loadDashboard() {
        try {
            document.getElementById('totalBooks').textContent = '...';
            document.getElementById('totalOrders').textContent = '...';
            document.getElementById('totalRevenue').textContent = '...';
            document.getElementById('totalUsers').textContent = '...';

            // Load statistics từ backend
            const statsRes = await api.getAdminDashboardStats();
            if (statsRes.data) {
                document.getElementById('totalBooks').textContent = statsRes.data.totalBooks || '0';
                document.getElementById('totalOrders').textContent = statsRes.data.totalOrders || '0';
                document.getElementById('totalUsers').textContent = statsRes.data.totalUsers || '0';
                document.getElementById('totalRevenue').textContent = this.formatPrice(statsRes.data.totalRevenue || 0) + 'đ';
            }

            // Load recent orders
            const recentRes = await api.getAdminOrders(0, 5);
            const mockOrders = recentRes.data?.content || [];
            this.renderRecentOrders(mockOrders);
        } catch (error) {
            console.error('Error loading dashboard:', error);
            this.showError('Lỗi tải dữ liệu Dashboard');
        }
    }

    renderRecentOrders(orders) {
        const tbody = document.getElementById('recentOrdersTable');
        if (!orders || orders.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-gray-500 py-4">Chưa có đơn hàng nào</td></tr>';
            return;
        }
        tbody.innerHTML = orders.map(order => `
            <tr>
                <td class="font-bold">${order.id}</td>
                <td>${order.recipientName || order.user.firstName + ' ' + order.user.lastName || 'Khách hàng'}</td>
                <td class="text-red-600 font-semibold">${this.formatPrice(order.totalAmount || 0)}đ</td>
                <td><span class="badge ${this.getStatusBadgeClass(order.status)}">${this.getStatusLabel(order.status)}</span></td>
                <td>${new Date(order.createdAt).toLocaleDateString('vi-VN')}</td>
            </tr>
        `).join('');
    }

    async loadBooks(append = false) {
        if (!this.booksState) {
            this.booksState = {
                page: 1, size: 20, hasMore: true, loading: false, search: '', category: ''
            };
        }
        
        if (this.booksState.loading) return;
        
        if (!append) {
            this.booksState.page = 1;
            this.booksState.hasMore = true;
        } else {
            if (!this.booksState.hasMore) return;
            this.booksState.page++;
        }

        this.booksState.loading = true;

        try {
            const tableBody = document.getElementById('booksTable');
            if (!append) {
                tableBody.innerHTML = '<tr><td colspan="7" class="text-center py-8">Đang tải dữ liệu...</td></tr>';
            } else {
                const tr = document.createElement('tr');
                tr.id = 'booksLoadingRow';
                tr.innerHTML = '<td colspan="7" class="text-center py-4 text-gray-500">Đang tải thêm...</td>';
                tableBody.appendChild(tr);
            }
            
            const params = { 
                page: this.booksState.page, 
                size: this.booksState.size,
                search: this.booksState.search,
                category: this.booksState.category
            };
            const res = await api.getBooks(params);
            const books = Array.isArray(res.data?.data) ? res.data.data : Array.isArray(res.data) ? res.data : [];

            if (append) {
                const loadingRow = document.getElementById('booksLoadingRow');
                if (loadingRow) loadingRow.remove();
            }

            if (!books || books.length === 0) {
                this.booksState.hasMore = false;
                if (!append) {
                    tableBody.innerHTML = '<tr><td colspan="7" class="text-center py-8 text-gray-500">Không có sách nào</td></tr>';
                }
            } else {
                if (books.length < this.booksState.size) {
                    this.booksState.hasMore = false;
                }
                const html = books.map(book => {
                    const now = new Date();
                    const isFlashSale = book.isFlashSale && book.flashSaleEndTime && new Date(book.flashSaleEndTime) > now;
                    const flashBadge = isFlashSale
                        ? `<span style="background:#ff6b00;color:#fff;padding:2px 7px;border-radius:12px;font-size:11px;font-weight:700;display:inline-block;margin-left:4px">🔥 -${book.flashSaleDiscountPercent}%</span>`
                        : '<span style="color:#9e9e9e;font-size:12px">—</span>';
                    const displayPrice = isFlashSale
                        ? `<span style="text-decoration:line-through;color:#9e9e9e;font-size:12px">${this.formatPrice(book.listPrice||0)}đ</span><br><strong style="color:#d32f2f">${this.formatPrice(book.flashSalePrice||0)}đ</strong>`
                        : `${this.formatPrice(book.listPrice||book.price||0)}đ`;
                    return `
                    <tr>
                        <td style="font-size:11px;color:#888;max-width:80px;overflow:hidden;text-overflow:ellipsis">${book.id || '-'}</td>
                        <td class="font-semibold text-gray-800" style="max-width:180px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap" title="${book.title}">${book.title}</td>
                        <td style="color:#555">${book.author || book.authors || '-'}</td>
                        <td>${book.categoryName || book.categories || '-'}</td>
                        <td class="font-medium">${displayPrice}</td>
                        <td>${flashBadge}</td>
                        <td style="font-weight:600;color:${(book.stockQuantity||0) > 10 ? '#388e3c' : '#d32f2f'}">${book.stockQuantity !== undefined ? book.stockQuantity : '-'}</td>
                        <td>
                            <button onclick="editBook('${book.id || book.bookId}')"
                                style="background:#1976d2;color:#fff;border:none;padding:5px 12px;border-radius:6px;cursor:pointer;font-size:13px;margin-right:4px">
                                <i class="fas fa-edit"></i> Sửa
                            </button>
                            <button onclick="deleteBook('${book.id || book.bookId}')"
                                style="background:#fce4ec;color:#d32f2f;border:1px solid #ffcdd2;padding:5px 10px;border-radius:6px;cursor:pointer;font-size:13px">
                                <i class="fas fa-trash"></i>
                            </button>
                        </td>
                    </tr>`;
                }).join('');

                
                if (append) {
                    tableBody.insertAdjacentHTML('beforeend', html);
                } else {
                    tableBody.innerHTML = html;
                }
            }

            if (!this.categoriesLoaded) {
                await this.loadCategoriesForFilter();
                this.categoriesLoaded = true;
            }
        } catch (error) {
            console.error('Error loading books:', error);
            const tableBody = document.getElementById('booksTable');
            if (append) {
                const loadingRow = document.getElementById('booksLoadingRow');
                if (loadingRow) loadingRow.remove();
            } else {
                tableBody.innerHTML = '<tr><td colspan="7" class="text-center py-8 text-red-500">Lỗi tải dữ liệu</td></tr>';
            }
        } finally {
            this.booksState.loading = false;
        }
    }

    async loadCategoriesForFilter() {
        try {
            const res = await api.getCategories();
            const categories = res.data || [];
            const select = document.getElementById('categoryFilter');
            if (select) {
                const currentValue = select.value;
                select.innerHTML = '<option value="">Tất Cả Danh Mục</option>' + 
                    categories.map(cat => `<option value="${cat.id}">${cat.name}</option>`).join('');
                select.value = currentValue;
            }
        } catch(e) {}
    }

    async loadCategories() {
        try {
            const tableBody = document.getElementById('categoriesTable');
            tableBody.innerHTML = '<tr><td colspan="4" class="text-center py-8">Đang tải dữ liệu...</td></tr>';
            
            const res = await api.getCategories();
            const categories = res.data || [];

            if (!categories || categories.length === 0) {
                tableBody.innerHTML = '<tr><td colspan="4" class="text-center py-8 text-gray-500">Không có danh mục nào</td></tr>';
            } else {
                tableBody.innerHTML = categories.map(cat => `
                    <tr>
                        <td>${cat.id}</td>
                        <td class="font-bold">${cat.name}</td>
                        <td>-</td>
                        <td>
                            <button onclick="editCategory(${cat.id})" class="text-blue-600 hover:underline mr-2">Sửa</button>
                            <button onclick="deleteCategory(${cat.id})" class="text-red-600 hover:underline">Xóa</button>
                        </td>
                    </tr>
                `).join('');
            }

            // Populate category select options
            const categorySelect = document.getElementById('bookCategory');
            categorySelect.innerHTML = categories.map(cat => 
                `<option value="${cat.id}">${cat.name}</option>`
            ).join('');
        } catch (error) {
            console.error('Error loading categories:', error);
            document.getElementById('categoriesTable').innerHTML = '<tr><td colspan="4" class="text-center py-8 text-red-500">Lỗi tải dữ liệu</td></tr>';
        }
    }

    async loadOrders(append = false) {
        if (!this.ordersState) {
            this.ordersState = { page: 0, size: 20, hasMore: true, loading: false };
        }
        if (this.ordersState.loading) return;
        
        if (!append) {
            this.ordersState.page = 0;
            this.ordersState.hasMore = true;
        } else {
            if (!this.ordersState.hasMore) return;
            this.ordersState.page++;
        }
        
        this.ordersState.loading = true;
        
        try {
            const tbody = document.getElementById('ordersTable');
            if (!append) {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center py-8">Đang tải dữ liệu đơn hàng...</td></tr>';
            } else {
                const tr = document.createElement('tr');
                tr.id = 'ordersLoadingRow';
                tr.innerHTML = '<td colspan="6" class="text-center py-4 text-gray-500">Đang tải thêm...</td>';
                tbody.appendChild(tr);
            }
            
            const res = await api.getAdminOrders(this.ordersState.page, this.ordersState.size);
            const orders = res.data?.content || [];

            if (append) {
                const loadingRow = document.getElementById('ordersLoadingRow');
                if (loadingRow) loadingRow.remove();
            }

            if (!orders || orders.length === 0) {
                this.ordersState.hasMore = false;
                if (!append) {
                    tbody.innerHTML = '<tr><td colspan="6" class="text-center py-8 text-gray-500">Chưa có đơn hàng nào</td></tr>';
                }
            } else {
                if (orders.length < this.ordersState.size) {
                    this.ordersState.hasMore = false;
                }
                const html = orders.map(order => `
                    <tr>
                        <td class="font-bold">${order.orderCode || order.id}</td>
                        <td>${order.recipientName || 'Khách hàng'}</td>
                        <td class="text-red-600 font-semibold">${this.formatPrice(order.totalAmount || 0)}đ</td>
                        <td><span class="badge ${this.getStatusBadgeClass(order.status)}">${this.getStatusLabel(order.status)}</span></td>
                        <td>${new Date(order.createdAt).toLocaleDateString('vi-VN')}</td>
                        <td>
                            <button onclick="viewOrder('${order.id}')" class="text-blue-600 hover:underline mx-1">Xem</button>
                            <select onchange="updateOrderStatus('${order.id}', this.value, this)" style="padding: 4px 8px; border: 1px solid #ccc; border-radius: 4px; font-size: 13px; margin-left: 5px; cursor: pointer; outline: none;">
                                <option value="" disabled selected>Đổi trạng thái...</option>
                                <option value="PENDING">Chờ Xử Lý</option>
                                <option value="CONFIRMED">Đã Xác Nhận</option>
                                <option value="SHIPPED">Đang Giao</option>
                                <option value="DELIVERED">Đã Giao</option>
                                <option value="CANCELLED">Hủy</option>
                            </select>
                        </td>
                    </tr>
                `).join('');
                
                if (append) {
                    tbody.insertAdjacentHTML('beforeend', html);
                } else {
                    tbody.innerHTML = html;
                }
            }
        } catch (error) {
            console.error('Error loading orders:', error);
            const tbody = document.getElementById('ordersTable');
            if (append) {
                const loadingRow = document.getElementById('ordersLoadingRow');
                if (loadingRow) loadingRow.remove();
            } else {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center py-8 text-red-500">Lỗi tải dữ liệu đơn hàng</td></tr>';
            }
        } finally {
            this.ordersState.loading = false;
        }
    }

    async loadUsers(append = false) {
        if (!this.usersState) {
            this.usersState = { page: 0, size: 20, hasMore: true, loading: false };
        }
        if (this.usersState.loading) return;
        
        if (!append) {
            this.usersState.page = 0;
            this.usersState.hasMore = true;
        } else {
            if (!this.usersState.hasMore) return;
            this.usersState.page++;
        }
        
        this.usersState.loading = true;
        
        try {
            const tbody = document.getElementById('usersTable');
            if (!append) {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center py-8">Đang tải dữ liệu người dùng...</td></tr>';
            } else {
                const tr = document.createElement('tr');
                tr.id = 'usersLoadingRow';
                tr.innerHTML = '<td colspan="6" class="text-center py-4 text-gray-500">Đang tải thêm...</td>';
                tbody.appendChild(tr);
            }
            
            const res = await api.getAllUsers(this.usersState.page, this.usersState.size);
            const users = res.data?.content || [];

            if (append) {
                const loadingRow = document.getElementById('usersLoadingRow');
                if (loadingRow) loadingRow.remove();
            }

            if (!users || users.length === 0) {
                this.usersState.hasMore = false;
                if (!append) {
                    tbody.innerHTML = '<tr><td colspan="6" class="text-center py-8 text-gray-500">Chưa có người dùng nào</td></tr>';
                }
            } else {
                if (users.length < this.usersState.size) {
                    this.usersState.hasMore = false;
                }
                const html = users.map(user => `
                    <tr>
                        <td>${user.id}</td>
                        <td class="font-semibold">${user.firstName || ''} ${user.lastName || ''}</td>
                        <td>${user.email || '-'}</td>
                        <td>${user.phone || '-'}</td>
                        <td>${user.createdAt || '-'}</td>
                        <td>
                            <button onclick="deleteUser(${user.id})" class="text-red-600 hover:underline font-semibold">
                                Xóa
                            </button>
                        </td>
                    </tr>
                `).join('');
                
                if (append) {
                    tbody.insertAdjacentHTML('beforeend', html);
                } else {
                    tbody.innerHTML = html;
                }
            }
        } catch (error) {
            console.error('Error loading users:', error);
            const tbody = document.getElementById('usersTable');
            if (append) {
                const loadingRow = document.getElementById('usersLoadingRow');
                if (loadingRow) loadingRow.remove();
            } else {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center py-8 text-red-500">Lỗi tải người dùng</td></tr>';
            }
        } finally {
            this.usersState.loading = false;
        }
    }

    async loadReviews(append = false) {
        if (!this.reviewsState) {
            this.reviewsState = { page: 0, size: 20, hasMore: true, loading: false };
        }
        if (this.reviewsState.loading) return;
        
        if (!append) {
            this.reviewsState.page = 0;
            this.reviewsState.hasMore = true;
        } else {
            if (!this.reviewsState.hasMore) return;
            this.reviewsState.page++;
        }
        
        this.reviewsState.loading = true;

        try {
            const tbody = document.getElementById('reviewsTable');
            if (!append) {
                tbody.innerHTML = '<tr><td colspan="5" class="text-center py-8">Đang tải đánh giá...</td></tr>';
            } else {
                const tr = document.createElement('tr');
                tr.id = 'reviewsLoadingRow';
                tr.innerHTML = '<td colspan="5" class="text-center py-4 text-gray-500">Đang tải thêm...</td>';
                tbody.appendChild(tr);
            }
            
            const res = await api.getAdminReviewSummaries(this.reviewsState.page, this.reviewsState.size);
            const summaries = res.data?.content || [];

            if (append) {
                const loadingRow = document.getElementById('reviewsLoadingRow');
                if (loadingRow) loadingRow.remove();
            }

            if (!summaries || summaries.length === 0) {
                this.reviewsState.hasMore = false;
                if (!append) {
                    tbody.innerHTML = '<tr><td colspan="5" class="text-center py-8 text-gray-500">Chưa có sách nào có đánh giá</td></tr>';
                }
            } else {
                if (summaries.length < this.reviewsState.size) {
                    this.reviewsState.hasMore = false;
                }
                const html = summaries.map(summary => `
                    <tr>
                        <td>${summary.bookId}</td>
                        <td class="font-semibold text-gray-800">${summary.bookTitle || summary.bookId || '-'}</td>
                        <td class="text-center font-bold text-blue-600">${summary.reviewCount || 0}</td>
                        <td class="text-yellow-400 text-sm">${'⭐'.repeat(Math.round(summary.averageRating || 0))} (${(summary.averageRating || 0).toFixed(1)})</td>
                        <td>
                            <button onclick="viewBookReviews('${summary.bookId}', '${(summary.bookTitle || '').replace(/'/g, "\\'")}')" class="text-blue-600 hover:underline font-semibold flex items-center gap-1">
                                Xem Đánh Giá
                            </button>
                        </td>
                    </tr>
                `).join('');
                
                if (append) {
                    tbody.insertAdjacentHTML('beforeend', html);
                } else {
                    tbody.innerHTML = html;
                }
            }
        } catch (error) {
            console.error('Error loading reviews:', error);
            const tbody = document.getElementById('reviewsTable');
            if (append) {
                const loadingRow = document.getElementById('reviewsLoadingRow');
                if (loadingRow) loadingRow.remove();
            } else {
                tbody.innerHTML = '<tr><td colspan="5" class="text-center py-8 text-red-500">Lỗi tải dữ liệu đánh giá</td></tr>';
            }
        } finally {
            this.reviewsState.loading = false;
        }
    }

    showBookModal(bookId) {
        const modal = document.getElementById('bookModal');
        const title = document.getElementById('bookModalTitle');
        
        if (bookId) {
            title.textContent = 'Chỉnh Sửa Sách';
            // Load book data
            const book = MOCK_DATA.books.find(b => b.id === bookId);
            if (book) {
                document.getElementById('bookTitle').value = book.title;
                document.getElementById('bookAuthor').value = book.author;
                document.getElementById('bookCategory').value = book.categoryId;
                document.getElementById('bookPrice').value = book.price;
                document.getElementById('bookStock').value = 15;
                document.getElementById('bookDescription').value = book.description;
            }
        } else {
            title.textContent = 'Thêm Sách Mới';
            document.getElementById('bookForm').reset();
        }
        
        modal.classList.remove('hidden');
    }

    showCategoryModal() {
        document.getElementById('categoryForm').reset();
        document.getElementById('categoryModal').classList.remove('hidden');
    }

    async saveBook() {
        admin.showNotification("Chức năng thêm sách đang được phát triển bên Backend!");
    }

    async saveCategory() {
        admin.showNotification("Chức năng thêm danh mục đang được phát triển backend!");
    }

    getStatusLabel(status) {
        if (!status) return 'Không rõ';
        const labels = {
            'PENDING': 'Chờ Xử Lý',
            'PENDING_PAYMENT': 'Chờ Thanh Toán',
            'CONFIRMED': 'Đã Xác Nhận',
            'SHIPPED': 'Đang Giao',
            'DELIVERED': 'Đã Giao',
            'CANCELLED': 'Đã Hủy',
            'pending': 'Chờ Xử Lý',
            'confirmed': 'Đã Xác Nhận',
            'shipped': 'Đang Giao',
            'delivered': 'Đã Giao',
            'cancelled': 'Đã Hủy'
        };
        return labels[status.toUpperCase()] || status;
    }

    getStatusBadgeClass(status) {
        if (!status) return 'badge-secondary';
        const classes = {
            'PENDING': 'badge-warning',
            'PENDING_PAYMENT': 'badge-warning',
            'CONFIRMED': 'badge-primary',
            'SHIPPED': 'badge-primary',
            'DELIVERED': 'badge-success',
            'CANCELLED': 'badge-danger',
            'pending': 'badge-warning',
            'confirmed': 'badge-primary',
            'shipped': 'badge-primary',
            'delivered': 'badge-success',
            'cancelled': 'badge-danger'
        };
        return classes[status.toUpperCase()] || 'badge-primary';
    }

    formatPrice(price) {
        return new Intl.NumberFormat('vi-VN').format(price);
    }

    showNotification(message) {
        const notification = document.createElement('div');
        notification.style.cssText = 'position:fixed;bottom:24px;right:24px;background:#388e3c;color:white;padding:12px 24px;border-radius:10px;font-weight:600;z-index:9999;box-shadow:0 4px 20px rgba(56,142,60,.4)';
        notification.textContent = message;
        document.body.appendChild(notification);
        setTimeout(() => notification.remove(), 3500);
    }

    async saveHomeLayout() {
        const id = document.getElementById('layoutId').value;
        const data = {
            title: document.getElementById('layoutTitle').value,
            itemCount: parseInt(document.getElementById('layoutItemCount').value),
            displayOrder: parseInt(document.getElementById('layoutOrder').value),
            active: document.getElementById('layoutActive').value === 'true'
        };

        try {
            await api.updateAdminHomeLayout(id, data);
            this.showNotification('Cập nhật layout thành công!');
            closeLayoutModal();
            this.loadHomeLayout();
        } catch (error) {
            console.error('Error saving layout:', error);
            this.showError('Lỗi cập nhật cấu hình layout');
        }
    }

    showError(message) {
        const notification = document.createElement('div');
        notification.style.cssText = 'position:fixed;bottom:24px;right:24px;background:#ef4444;color:white;padding:12px 24px;border-radius:10px;font-weight:600;z-index:9999;box-shadow:0 4px 20px rgba(239,68,68,.4);animation:slideInRight .3s ease';
        notification.textContent = message;
        document.body.appendChild(notification);
        setTimeout(() => notification.remove(), 3000);
    }

    // ====================================================
    // FLASH SALE MANAGEMENT
    // ====================================================

    async loadFlashSaleManagement() {
        try {
            const tbody = document.getElementById('flashSaleTableBody');
            if (!tbody) return;
            tbody.innerHTML = '<tr><td colspan="7" style="padding:40px;text-align:center"><i class="fas fa-circle-notch fa-spin" style="color:#d32f2f"></i> Đang tải...</td></tr>';

            const res = await api.getAdminFlashSaleBooks();
            const books = res.data || [];

            if (books.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" style="padding:40px;text-align:center;color:#9e9e9e">Đang chưa có sách nào trong Flash Sale</td></tr>';
            } else {
                tbody.innerHTML = books.map(book => {
                    const now = new Date();
                    const endTime = book.flashSaleEndTime ? new Date(book.flashSaleEndTime) : null;
                    const isActive = book.isFlashSale && endTime && endTime > now;
                    const remaining = endTime ? this.getTimeRemaining(endTime) : '';
                    return `
                    <tr>
                        <td style="max-width:200px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap" title="${book.title}">${book.title}</td>
                        <td class="font-medium" style="color:#1976d2">${this.formatPrice(book.listPrice || 0)}đ</td>
                        <td style="color:#d32f2f;font-weight:600">${this.formatPrice(book.flashSalePrice || 0)}đ</td>
                        <td><span style="background:#ff6b00;color:#fff;padding:2px 8px;border-radius:20px;font-size:12px;font-weight:700">-${book.flashSaleDiscountPercent || 0}%</span></td>
                        <td style="font-size:12px">${endTime ? endTime.toLocaleString('vi-VN') : '-'}</td>
                        <td>
                            <span style="padding:3px 10px;border-radius:20px;font-size:12px;font-weight:600;${isActive ? 'background:#e8f5e9;color:#388e3c' : 'background:#fce4ec;color:#d32f2f'}">
                                ${isActive ? 'ð¥ Đang chạy' + (remaining ? ' - còn ' + remaining : '') : '⏰ Hết hạn'}
                            </span>
                        </td>
                        <td>
                            <button onclick="stopFlashSale('${book.id}', '${(book.title||'').replace(/'/g, "\\'")}')"
                                style="background:#d32f2f;color:#fff;border:none;padding:5px 12px;border-radius:6px;cursor:pointer;font-size:13px">
                                <i class="fas fa-stop"></i> Dừng
                            </button>
                        </td>
                    </tr>`;
                }).join('');
            }
        } catch(e) {
            console.error('Error loading flash sale books:', e);
            const tbody = document.getElementById('flashSaleTableBody');
            if (tbody) tbody.innerHTML = '<tr><td colspan="7" style="padding:40px;text-align:center;color:#d32f2f">Lỗi tải dữ liệu flash sale</td></tr>';
        }
    }

    getTimeRemaining(endTime) {
        const now = new Date();
        const diff = endTime - now;
        if (diff <= 0) return '';
        const h = Math.floor(diff / 3600000);
        const m = Math.floor((diff % 3600000) / 60000);
        if (h > 0) return `${h}h ${m}m`;
        return `${m} phút`;
    }
}

// Initialize admin dashboard
let admin;

document.addEventListener('DOMContentLoaded', () => {
    admin = new AdminDashboard();
});

// Global functions for event handlers
function closeBookModal() {
    document.getElementById('bookModal').classList.add('hidden');
}

function closeCategoryModal() {
    document.getElementById('categoryModal').classList.add('hidden');
}

function logout() {
    localStorage.removeItem(CONFIG.STORAGE_KEYS.TOKEN);
    localStorage.removeItem(CONFIG.STORAGE_KEYS.USER);
    window.location.href = '../index.html';
}

// ============================================================
// Sửa Giá Sách
// ============================================================

let _editingBookId = null;

async function editBook(bookId) {
    _editingBookId = bookId;
    try {
        const res = await api.getBookById(bookId);
        const book = res.data || res;
        if (!book) { admin.showError('Không tìm thấy sách!'); return; }

        document.getElementById('editPriceBookId').value = bookId;
        document.getElementById('editPriceBookTitle').textContent = book.title || bookId;
        document.getElementById('editPriceCurrentPrice').textContent =
            new Intl.NumberFormat('vi-VN').format(book.listPrice || 0) + 'đ' + 
            ' (Tồn: ' + (book.stockQuantity !== undefined ? book.stockQuantity : 0) + ')';
        document.getElementById('editPriceInput').value = book.listPrice || 0;
        document.getElementById('editStockInputModal').value = book.stockQuantity !== undefined ? book.stockQuantity : 0;

        // Điền sẵn thông tin flash sale nếu đang có
        if (book.isFlashSale) {
            document.getElementById('flashSaleDiscountPercent').value = book.flashSaleDiscountPercent || 10;
            if (book.flashSaleEndTime) {
                // Format cho datetime-local input
                const dt = new Date(book.flashSaleEndTime);
                document.getElementById('flashSaleEndTime').value = dt.toISOString().slice(0, 16);
            }
        } else {
            document.getElementById('flashSaleDiscountPercent').value = 10;
            // Default: kết thúc sau 24h
            const tomorrow = new Date(Date.now() + 86400000);
            document.getElementById('flashSaleEndTime').value = tomorrow.toISOString().slice(0, 16);
        }

        document.getElementById('editPriceModal').classList.remove('hidden');
    } catch(e) {
        console.error(e);
        admin.showError('Lỗi tải thông tin sách');
    }
}

async function saveBookPrice() {
    const bookId = document.getElementById('editPriceBookId').value;
    const newPrice = parseFloat(document.getElementById('editPriceInput').value);
    const newStock = parseInt(document.getElementById('editStockInputModal').value);
    
    if (!bookId || isNaN(newPrice) || newPrice < 0) {
        admin.showError('Vui lòng nhập giá hợp lệ!');
        return;
    }
    if (isNaN(newStock) || newStock < 0) {
        admin.showError('Vui lòng nhập số lượng tồn kho hợp lệ!');
        return;
    }
    try {
        await api.updateBookPrice(bookId, newPrice, newStock);
        admin.showNotification('✅ Cập nhật giá thành công!');
        closeEditPriceModal();
        admin.loadBooks(false);
    } catch(e) {
        console.error(e);
        admin.showError('Lỗi cập nhật giá: ' + (e.message || ''));
    }
}

async function saveFlashSaleFromModal() {
    const bookId = document.getElementById('editPriceBookId').value;
    const discountPercent = parseInt(document.getElementById('flashSaleDiscountPercent').value);
    const endTimeVal = document.getElementById('flashSaleEndTime').value;

    if (!bookId || !discountPercent || discountPercent < 1 || discountPercent > 99) {
        admin.showError('Phần trăm giảm phải từ 1-99!');
        return;
    }
    if (!endTimeVal) {
        admin.showError('Vui lòng chọn thời gian kết thúc!');
        return;
    }
    const endTime = new Date(endTimeVal).toISOString();
    try {
        await api.enableFlashSale(bookId, discountPercent, null, endTime);
        admin.showNotification('🔥 Đã bật Flash Sale thành công!');
        closeEditPriceModal();
        admin.loadBooks(false);
        if (admin.currentPage === 'flash-sale') admin.loadFlashSaleManagement();
    } catch(e) {
        console.error(e);
        admin.showError('Lỗi bật flash sale: ' + (e.message || ''));
    }
}

function closeEditPriceModal() {
    document.getElementById('editPriceModal').classList.add('hidden');
    _editingBookId = null;
}

function editCategory(categoryId) {
    admin.showNotification("Chức năng này đang được phát triển!");
}

function deleteCategory(categoryId) {
    admin.showNotification("Backend hiện chưa có API xóa danh mục.");
}

// ============================================================
// Flash Sale Management - Global Functions
// ============================================================

async function stopFlashSale(bookId, bookTitle) {
    if (!confirm(`Bạn có chắc muốn dừng Flash Sale cho sách:\n"${bookTitle}"?\n\nGiá sẽ trở về giá gốc.`)) return;
    try {
        await api.disableFlashSale(bookId);
        admin.showNotification('⏹ Đã dừng Flash Sale. Giá đã về bình thường!');
        admin.loadFlashSaleManagement();
    } catch(e) {
        console.error(e);
        admin.showError('Lỗi dừng flash sale: ' + (e.message || ''));
    }
}

function viewOrder(orderId) {
    admin.showNotification("Tính năng xem chi tiết đơn hàng đang được cập nhật!");
}

async function updateOrderStatus(orderId, status, selectElement) {
    if (!status) return;
    if (confirm(`Bạn có chắc muốn đổi trạng thái đơn hàng sang: ${status}?`)) {
        try {
            await api.updateAdminOrderStatus(orderId, status.toUpperCase());
            admin.showNotification('Đã cập nhật trạng thái đơn hàng!');
            admin.loadOrders(); // Tải lại bảng để cập nhật màu Badge
        } catch (e) {
            console.error(e);
            admin.showError('Lỗi cập nhật trạng thái đơn hàng.');
            if (selectElement) selectElement.selectedIndex = 0;
        }
    } else {
        if (selectElement) selectElement.selectedIndex = 0;
    }
}

function viewUser(userId) {
    console.log('View user:', userId);
}

async function deleteUser(userId) {
    if (confirm('Bạn có chắc chắn muốn xóa người dùng này?')) {
        try {
            await api.deleteUserByAdmin(userId);
            admin.showNotification('Đã xóa người dùng thành công!');
            admin.loadUsers();
        } catch (e) {
            console.error(e);
            admin.showError('Lỗi hệ thống khi xóa người dùng.');
        }
    }
}

let currentBookReviewsBookId = null;

async function viewBookReviews(bookId, bookTitle) {
    currentBookReviewsBookId = bookId;
    document.getElementById('bookReviewsModalTitle').textContent = `Đánh Giá Của Sách: ${bookTitle}`;
    document.getElementById('bookReviewsModal').classList.remove('hidden');
    
    const tbody = document.getElementById('bookReviewsTableBody');
    tbody.innerHTML = '<tr><td colspan="6" class="text-center py-8">Đang tải đánh giá...</td></tr>';
    
    try {
        const res = await api.getReviews(bookId);
        let reviews = [];
        if (res.data?.reviews) reviews = res.data.reviews;
        else if (res.data?.content) reviews = res.data.content;
        else if (Array.isArray(res.data)) reviews = res.data;
        
        if (!reviews || reviews.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center py-8 text-gray-500">Chưa có đánh giá nào</td></tr>';
        } else {
            tbody.innerHTML = reviews.map(review => `
                <tr>
                    <td>${review.id}</td>
                    <td>${review.userName || 'Khách'}</td>
                    <td class="text-yellow-400 text-sm">${'⭐'.repeat(review.rating || 0)}</td>
                    <td class="line-clamp-2" title="${review.comment || ''}">${review.comment || ''}</td>
                    <td>${review.createdAt ? new Date(review.createdAt).toLocaleDateString('vi-VN') : '-'}</td>
                    <td>
                        <button onclick="deleteReviewFromModal(${review.id})" class="text-red-600 hover:underline font-semibold flex items-center gap-1">
                            Xóa
                        </button>
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error('Error loading book reviews:', e);
        tbody.innerHTML = '<tr><td colspan="6" class="text-center py-8 text-red-500">Lỗi tải dữ liệu</td></tr>';
    }
}

function closeBookReviewsModal() {
    document.getElementById('bookReviewsModal').classList.add('hidden');
    currentBookReviewsBookId = null;
    if (admin && typeof admin.loadReviews === 'function') {
        admin.loadReviews();
    }
}

async function deleteReviewFromModal(reviewId) {
    if (confirm('Bạn có chắc chắn muốn xóa đánh giá này khỏi hệ thống?')) {
        try {
            await api.deleteAdminReview(reviewId);
            if (admin) admin.showNotification('Đã xóa đánh giá thành công!');
            if (currentBookReviewsBookId) {
                viewBookReviews(currentBookReviewsBookId, document.getElementById('bookReviewsModalTitle').textContent.replace('Đánh Giá Của Sách: ', ''));
            }
        } catch (e) {
            console.error(e);
            if (admin) admin.showError('Lỗi hệ thống khi xóa đánh giá.');
        }
    }
}

async function deleteReview(reviewId) {
    if (confirm('Bạn có chắc chắn muốn xóa đánh giá này khỏi hệ thống?')) {
        try {
            await api.deleteAdminReview(reviewId);
            admin.showNotification('Đã xóa đánh giá thành công!');
            admin.loadReviews();
        } catch (e) {
            console.error(e);
            admin.showError('Lỗi hệ thống khi xóa đánh giá.');
        }
    }
}

// Layout Functions
async function editLayout(id) {
    try {
        const res = await api.getAdminHomeLayout();
        const item = (res.data || []).find(i => i.id === id);
        if (!item) return;

        document.getElementById('layoutId').value = item.id;
        document.getElementById('layoutTitle').value = item.title;
        document.getElementById('layoutItemCount').value = item.itemCount;
        document.getElementById('layoutOrder').value = item.displayOrder;
        document.getElementById('layoutActive').value = item.active.toString();

        document.getElementById('layoutModal').classList.remove('hidden');
    } catch (e) {
        admin.showError('Lỗi tải thông tin layout');
    }
}

function closeLayoutModal() {
    document.getElementById('layoutModal').classList.add('hidden');
}

// ====================================================
// VOUCHER MANAGEMENT
// ====================================================

AdminDashboard.prototype.loadVouchers = async function() {
    try {
        const tbody = document.getElementById('vouchersTableBody');
        if (!tbody) return;
        tbody.innerHTML = '<tr><td colspan="7" style="padding:40px;text-align:center"><i class="fas fa-circle-notch fa-spin" style="color:#2e7d32"></i> Đang tải...</td></tr>';

        const res = await api.getAdminVouchers();
        const vouchers = res || [];

        if (vouchers.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" style="padding:40px;text-align:center;color:#9e9e9e">Chưa có mã giảm giá nào</td></tr>';
        } else {
            tbody.innerHTML = vouchers.map(v => {
                const status = v.active ? '<span class="badge badge-success">Kích hoạt</span>' : '<span class="badge badge-danger">Khóa</span>';
                const limit = v.usageLimit > 0 ? `${v.usedCount}/${v.usageLimit}` : `${v.usedCount}/∞`;
                const maxAmount = v.maxDiscountAmount ? `<br><small style="color:#888">Tối đa: ${admin.formatPrice(v.maxDiscountAmount)}đ</small>` : '';
                const minOrder = v.minOrderValue ? `${admin.formatPrice(v.minOrderValue)}đ` : '0đ';
                const expiry = v.expiryDate ? new Date(v.expiryDate).toLocaleString('vi-VN') : 'Không giới hạn';
                
                // Escape JSON data to pass to function
                const voucherDataStr = JSON.stringify(v).replace(/"/g, '&quot;');
                
                return `
                <tr>
                    <td style="color:#888">${v.id}</td>
                    <td style="font-weight:700;color:#2e7d32">${v.code}</td>
                    <td style="font-weight:600">${v.discountPercent}%${maxAmount}</td>
                    <td>${minOrder}</td>
                    <td style="font-size:12px">${expiry}<br><small style="color:#1976d2">Đã dùng: ${limit}</small></td>
                    <td>${status}</td>
                    <td>
                        <button onclick="admin.openVoucherModal(${v.id}, '${voucherDataStr}')" class="btn btn-sm" style="background:#e3f2fd;color:#1976d2;border:none;padding:5px 10px;border-radius:4px;cursor:pointer;margin-right:4px">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button onclick="admin.deleteVoucher(${v.id})" class="btn btn-sm" style="background:#ffebee;color:#c62828;border:none;padding:5px 10px;border-radius:4px;cursor:pointer">
                            <i class="fas fa-trash"></i>
                        </button>
                    </td>
                </tr>`;
            }).join('');
        }
    } catch (e) {
        console.error('Lỗi tải mã giảm giá:', e);
        const tbody = document.getElementById('vouchersTableBody');
        if (tbody) tbody.innerHTML = '<tr><td colspan="7" style="padding:40px;text-align:center;color:#c62828">Lỗi tải dữ liệu</td></tr>';
    }
};

AdminDashboard.prototype.openVoucherModal = function(id = null, voucherDataStr = null) {
    document.getElementById('voucherForm').reset();
    document.getElementById('voucherId').value = id || '';
    
    if (id && voucherDataStr) {
        document.getElementById('voucherModalTitle').innerHTML = '<i class="fas fa-ticket-alt" style="margin-right:8px"></i>Cập Nhật Mã Giảm Giá';
        const v = JSON.parse(voucherDataStr);
        document.getElementById('voucherCode').value = v.code || '';
        document.getElementById('voucherPercent').value = v.discountPercent || 10;
        document.getElementById('voucherMaxAmount').value = v.maxDiscountAmount || '';
        document.getElementById('voucherMinOrder').value = v.minOrderValue || '';
        document.getElementById('voucherLimit').value = v.usageLimit || 0;
        
        if (v.expiryDate) {
            // Convert to datetime-local format YYYY-MM-DDThh:mm
            const date = new Date(v.expiryDate);
            const offset = date.getTimezoneOffset() * 60000;
            const localISOTime = (new Date(date - offset)).toISOString().slice(0, 16);
            document.getElementById('voucherExpiry').value = localISOTime;
        }
        
        document.getElementById('voucherActive').value = v.active !== false ? "true" : "false";
    } else {
        document.getElementById('voucherModalTitle').innerHTML = '<i class="fas fa-ticket-alt" style="margin-right:8px"></i>Thêm Mã Giảm Giá';
        document.getElementById('voucherActive').value = "true";
    }
    
    document.getElementById('voucherModal').classList.remove('hidden');
};

AdminDashboard.prototype.closeVoucherModal = function() {
    document.getElementById('voucherModal').classList.add('hidden');
};

AdminDashboard.prototype.saveVoucher = async function() {
    const id = document.getElementById('voucherId').value;
    const code = document.getElementById('voucherCode').value.trim();
    if (!code) return admin.showError('Vui lòng nhập mã code');
    
    const data = {
        code: code,
        discountPercent: parseInt(document.getElementById('voucherPercent').value) || 0,
        maxDiscountAmount: parseFloat(document.getElementById('voucherMaxAmount').value) || null,
        minOrderValue: parseFloat(document.getElementById('voucherMinOrder').value) || null,
        usageLimit: parseInt(document.getElementById('voucherLimit').value) || 0,
        active: document.getElementById('voucherActive').value === 'true'
    };
    
    const expiry = document.getElementById('voucherExpiry').value;
    if (expiry) {
        data.expiryDate = new Date(expiry).toISOString();
    } else {
        data.expiryDate = null;
    }
    
    try {
        if (id) {
            await api.updateAdminVoucher(id, data);
            admin.showNotification('Cập nhật mã giảm giá thành công');
        } else {
            await api.createAdminVoucher(data);
            admin.showNotification('Thêm mã giảm giá thành công');
        }
        admin.closeVoucherModal();
        admin.loadVouchers();
    } catch (e) {
        console.error(e);
        admin.showError('Lỗi lưu mã giảm giá: ' + (e.message || ''));
    }
};

AdminDashboard.prototype.deleteVoucher = async function(id) {
    if (!confirm('Bạn có chắc muốn xóa mã giảm giá này?')) return;
    try {
        await api.deleteAdminVoucher(id);
        admin.showNotification('Đã xóa mã giảm giá');
        admin.loadVouchers();
    } catch (e) {
        console.error(e);
        admin.showError('Lỗi xóa mã giảm giá');
    }
};

// Also hook it up to loadPageData
const originalLoadPageData = AdminDashboard.prototype.loadPageData;
AdminDashboard.prototype.loadPageData = async function(page) {
    await originalLoadPageData.call(this, page);
    if (page === 'vouchers') {
        await this.loadVouchers();
    }
};
