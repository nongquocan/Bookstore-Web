// ============================================================
// AI BOOK CHAT — Powered by Google Gemini (via Backend Proxy)
// ============================================================
(function () {
  'use strict';

  // Backend endpoint — API key Gemini được giữ an toàn trên server
  const AI_CHAT_URL = 'http://localhost:8080/api/ai/chat';

  // Keys cho sessionStorage
  const SESSION_KEY_HISTORY = 'bookstore_ai_history';
  const SESSION_KEY_HTML = 'bookstore_ai_html';
  const SESSION_KEY_STATE = 'bookstore_ai_state';

  let chatHistory = JSON.parse(sessionStorage.getItem(SESSION_KEY_HISTORY) || '[]');
  let isOpen = false; // Mặc định đóng khi mới vào web
  let isTyping = false;

  // ── Inject CSS ──────────────────────────────────────────────
  const style = document.createElement('style');
  style.textContent = `
    @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

    #ai-chat-widget * { box-sizing: border-box; font-family: 'Inter', sans-serif; }

    /* Nút mở chat */
    #ai-chat-toggle {
      position: fixed;
      bottom: 28px;
      right: 28px;
      width: 60px;
      height: 60px;
      border-radius: 50%;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      cursor: pointer;
      box-shadow: 0 8px 32px rgba(102,126,234,0.45);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 9998;
      transition: transform 0.3s cubic-bezier(.34,1.56,.64,1), box-shadow 0.3s;
      animation: chatBounce 2s ease-in-out 1s 2;
    }
    #ai-chat-toggle:hover {
      transform: scale(1.12);
      box-shadow: 0 12px 40px rgba(102,126,234,0.6);
    }
    #ai-chat-toggle .toggle-icon { font-size: 24px; transition: opacity 0.2s, transform 0.3s; color: #fff; }
    #ai-chat-toggle .toggle-icon.hidden { opacity: 0; transform: rotate(90deg) scale(0); position: absolute; }

    /* Badge notification */
    #ai-chat-badge {
      position: absolute; top: -4px; right: -4px;
      width: 20px; height: 20px; border-radius: 50%;
      background: #ff4757; color: #fff; font-size: 11px; font-weight: 700;
      display: flex; align-items: center; justify-content: center;
      border: 2px solid #fff; animation: badgePulse 1.5s infinite;
    }

    /* Cửa sổ chat */
    #ai-chat-box {
      position: fixed;
      bottom: 100px;
      right: 28px;
      width: 380px;
      max-height: 580px;
      background: #fff;
      border-radius: 20px;
      box-shadow: 0 24px 80px rgba(0,0,0,0.18), 0 4px 16px rgba(0,0,0,0.08);
      display: flex;
      flex-direction: column;
      z-index: 9999;
      overflow: hidden;
      transform-origin: bottom right;
      transform: scale(0.8) translateY(20px);
      opacity: 0;
      pointer-events: none;
      transition: transform 0.35s cubic-bezier(.34,1.56,.64,1), opacity 0.25s ease;
    }
    #ai-chat-box.open {
      transform: scale(1) translateY(0);
      opacity: 1;
      pointer-events: all;
    }

    /* Header */
    #ai-chat-header {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 16px 18px;
      display: flex;
      align-items: center;
      gap: 12px;
      flex-shrink: 0;
    }
    .ai-avatar {
      width: 42px; height: 42px; border-radius: 50%;
      background: rgba(255,255,255,0.25);
      display: flex; align-items: center; justify-content: center;
      font-size: 20px; flex-shrink: 0;
      border: 2px solid rgba(255,255,255,0.4);
    }
    .ai-header-info { flex: 1; min-width: 0; }
    .ai-header-name { color: #fff; font-weight: 700; font-size: 15px; }
    .ai-header-status { color: rgba(255,255,255,0.8); font-size: 12px; margin-top: 2px; display: flex; align-items: center; gap: 5px; }
    .status-dot { width: 7px; height: 7px; border-radius: 50%; background: #4ade80; display: inline-block; animation: statusPulse 2s infinite; }
    #ai-chat-close {
      background: rgba(255,255,255,0.2); border: none; cursor: pointer;
      width: 32px; height: 32px; border-radius: 50%;
      display: flex; align-items: center; justify-content: center;
      color: #fff; font-size: 16px; transition: background 0.2s;
      flex-shrink: 0;
    }
    #ai-chat-close:hover { background: rgba(255,255,255,0.35); }

    /* Quick actions */
    #ai-quick-actions {
      padding: 12px 14px 8px;
      display: flex; gap: 6px; flex-wrap: wrap; flex-shrink: 0;
      background: #f8f9ff; border-bottom: 1px solid #eef0ff;
    }
    .quick-btn {
      padding: 6px 12px; border-radius: 20px; font-size: 12px; font-weight: 500;
      border: 1.5px solid #c7d2fe; background: #fff; color: #4f46e5;
      cursor: pointer; transition: all 0.2s; white-space: nowrap;
    }
    .quick-btn:hover { background: #4f46e5; color: #fff; border-color: #4f46e5; }

    /* Messages */
    #ai-chat-messages {
      flex: 1; overflow-y: auto; padding: 14px; display: flex;
      flex-direction: column; gap: 12px; min-height: 0;
      scrollbar-width: thin; scrollbar-color: #c7d2fe transparent;
    }
    #ai-chat-messages::-webkit-scrollbar { width: 4px; }
    #ai-chat-messages::-webkit-scrollbar-thumb { background: #c7d2fe; border-radius: 4px; }

    .chat-msg { display: flex; gap: 8px; max-width: 100%; animation: msgSlideIn 0.3s ease; }
    .chat-msg.user { flex-direction: row-reverse; }

    .msg-avatar {
      width: 32px; height: 32px; border-radius: 50%; flex-shrink: 0;
      display: flex; align-items: center; justify-content: center; font-size: 14px;
    }
    .chat-msg.bot .msg-avatar { background: linear-gradient(135deg, #667eea, #764ba2); color: #fff; }
    .chat-msg.user .msg-avatar { background: #e0e7ff; color: #4f46e5; font-weight: 700; }

    .msg-bubble {
      padding: 10px 14px; border-radius: 16px; font-size: 14px; line-height: 1.55;
      max-width: calc(100% - 50px); word-wrap: break-word;
    }
    .chat-msg.bot .msg-bubble {
      background: #f1f3ff; color: #1e1b4b; border-bottom-left-radius: 4px;
    }
    .chat-msg.user .msg-bubble {
      background: linear-gradient(135deg, #667eea, #764ba2); color: #fff;
      border-bottom-right-radius: 4px;
    }

    /* Typing indicator */
    .typing-indicator { display: flex; gap: 4px; align-items: center; padding: 12px 14px; }
    .typing-dot {
      width: 8px; height: 8px; border-radius: 50%; background: #9ca3af;
      animation: typingDot 1.2s infinite;
    }
    .typing-dot:nth-child(2) { animation-delay: 0.2s; }
    .typing-dot:nth-child(3) { animation-delay: 0.4s; }

    /* Input */
    #ai-chat-input-area {
      padding: 12px 14px; border-top: 1px solid #eef0ff;
      display: flex; gap: 8px; align-items: flex-end; flex-shrink: 0;
      background: #fff;
    }
    #ai-chat-input {
      flex: 1; border: 1.5px solid #e0e7ff; border-radius: 12px;
      padding: 9px 13px; font-size: 14px; resize: none; outline: none;
      font-family: 'Inter', sans-serif; max-height: 100px; min-height: 40px;
      line-height: 1.5; transition: border-color 0.2s;
      color: #1e1b4b;
    }
    #ai-chat-input:focus { border-color: #667eea; }
    #ai-chat-input::placeholder { color: #9ca3af; }
    #ai-chat-send {
      width: 40px; height: 40px; border-radius: 12px;
      background: linear-gradient(135deg, #667eea, #764ba2);
      border: none; cursor: pointer; flex-shrink: 0;
      display: flex; align-items: center; justify-content: center;
      transition: opacity 0.2s, transform 0.2s; color: #fff; font-size: 16px;
    }
    #ai-chat-send:hover { opacity: 0.9; transform: scale(1.05); }
    #ai-chat-send:disabled { opacity: 0.5; cursor: not-allowed; transform: none; }

    /* Footer */
    #ai-chat-footer {
      text-align: center; padding: 6px; font-size: 11px; color: #9ca3af;
      background: #fafafa; border-top: 1px solid #f0f0f0; flex-shrink: 0;
    }
    #ai-chat-footer span { color: #667eea; font-weight: 600; }

    /* Mobile */
    @media (max-width: 480px) {
      #ai-chat-box { width: calc(100vw - 24px); right: 12px; bottom: 90px; max-height: 75vh; }
      #ai-chat-toggle { bottom: 20px; right: 16px; }
    }

    /* Animations */
    @keyframes chatBounce { 0%,100%{transform:translateY(0)} 50%{transform:translateY(-8px)} }
    @keyframes badgePulse { 0%,100%{transform:scale(1)} 50%{transform:scale(1.2)} }
    @keyframes statusPulse { 0%,100%{opacity:1} 50%{opacity:0.4} }
    @keyframes msgSlideIn { from{opacity:0;transform:translateY(8px)} to{opacity:1;transform:translateY(0)} }
    @keyframes typingDot { 0%,100%{transform:translateY(0);opacity:0.4} 50%{transform:translateY(-5px);opacity:1} }
  `;
  document.head.appendChild(style);

  // ── HTML ───────────────────────────────────────────────────
  const wrapper = document.createElement('div');
  wrapper.id = 'ai-chat-widget';
  wrapper.innerHTML = `
    <!-- Nút toggle -->
    <button id="ai-chat-toggle" aria-label="Mở chat tư vấn sách AI" title="Chat với AI tư vấn sách">
      <span class="toggle-icon" id="icon-chat">🤖</span>
      <span class="toggle-icon hidden" id="icon-close">✕</span>
      <span id="ai-chat-badge">1</span>
    </button>

    <!-- Cửa sổ chat -->
    <div id="ai-chat-box" role="dialog" aria-label="AI tư vấn sách">
      <div id="ai-chat-header">
        <div class="ai-avatar">🤖</div>
        <div class="ai-header-info">
          <div class="ai-header-name">BookAI Assistant</div>
          <div class="ai-header-status">
            <span class="status-dot"></span>
            Trực tuyến · Sẵn sàng tư vấn
          </div>
        </div>
        <button id="ai-chat-close" aria-label="Đóng chat">✕</button>
      </div>

      <div id="ai-quick-actions">
        <button class="quick-btn" data-msg="Gợi ý sách hay nhất cho tôi">✨ Gợi ý sách</button>
        <button class="quick-btn" data-msg="Tóm tắt sách Sapiens">📖 Tóm tắt sách</button>
        <button class="quick-btn" data-msg="Sách phát triển bản thân tốt nhất">🚀 Tự lực</button>
        <button class="quick-btn" data-msg="Sách lập trình hay nhất">💻 Công nghệ</button>
      </div>

      <div id="ai-chat-messages">
        <!-- Tin nhắn chào -->
        <div class="chat-msg bot">
          <div class="msg-avatar">🤖</div>
          <div class="msg-bubble">
            Xin chào! 👋 Tôi là <strong>BookAI</strong> — trợ lý tư vấn sách thông minh của BookStore.<br><br>
            Tôi có thể giúp bạn:<br>
            📚 <strong>Gợi ý sách</strong> phù hợp với sở thích<br>
            📝 <strong>Tóm tắt nội dung</strong> bất kỳ cuốn sách nào<br>
            🎯 <strong>Tư vấn</strong> sách theo mục đích đọc<br><br>
            Bạn muốn tìm loại sách gì hôm nay?
          </div>
        </div>
      </div>

      <div id="ai-chat-input-area">
        <textarea id="ai-chat-input" placeholder="Hỏi về sách bạn muốn..." rows="1" aria-label="Nhập câu hỏi"></textarea>
        <button id="ai-chat-send" aria-label="Gửi tin nhắn">
          <i class="fas fa-paper-plane"></i>
        </button>
      </div>

      <div id="ai-chat-footer">Powered by <span>Google Gemini</span> · BookStore AI</div>
    </div>
  `;
  document.body.appendChild(wrapper);

  // ── DOM refs ───────────────────────────────────────────────
  const toggleBtn = document.getElementById('ai-chat-toggle');
  const chatBox = document.getElementById('ai-chat-box');
  const closeBtn = document.getElementById('ai-chat-close');
  const messagesEl = document.getElementById('ai-chat-messages');
  const inputEl = document.getElementById('ai-chat-input');
  const sendBtn = document.getElementById('ai-chat-send');
  const badge = document.getElementById('ai-chat-badge');
  const iconChat = document.getElementById('icon-chat');
  const iconClose = document.getElementById('icon-close');

  // Khôi phục nội dung tin nhắn nếu có
  const savedHtml = sessionStorage.getItem(SESSION_KEY_HTML);
  if (savedHtml) {
    messagesEl.innerHTML = savedHtml;
    badge.style.display = 'none'; // Đã xem tin nhắn
  }

  // Khôi phục trạng thái đóng/mở
  if (sessionStorage.getItem(SESSION_KEY_STATE) === 'true') {
    openChat();
  }

  // ── Helpers ────────────────────────────────────────────────
  function openChat() {
    isOpen = true;
    sessionStorage.setItem(SESSION_KEY_STATE, 'true');
    chatBox.classList.add('open');
    iconChat.classList.add('hidden');
    iconClose.classList.remove('hidden');
    badge.style.display = 'none';
    setTimeout(() => {
      inputEl.focus();
      scrollToBottom();
    }, 350);
  }

  function closeChat() {
    isOpen = false;
    sessionStorage.setItem(SESSION_KEY_STATE, 'false');
    chatBox.classList.remove('open');
    iconChat.classList.remove('hidden');
    iconClose.classList.add('hidden');
  }

  function scrollToBottom() {
    messagesEl.scrollTop = messagesEl.scrollHeight;
  }

  function addMessage(text, role) {
    const div = document.createElement('div');
    div.className = `chat-msg ${role}`;
    const avatarIcon = role === 'bot' ? '🤖' : '👤';
    // Render markdown đơn giản
    const formatted = text
      .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
      .replace(/\*(.*?)\*/g, '<em>$1</em>')
      .replace(/\n/g, '<br>');
    div.innerHTML = `
      <div class="msg-avatar">${avatarIcon}</div>
      <div class="msg-bubble">${formatted}</div>
    `;
    messagesEl.appendChild(div);
    scrollToBottom();
    
    // Lưu lại giao diện chat vào SessionStorage
    sessionStorage.setItem(SESSION_KEY_HTML, messagesEl.innerHTML);
    
    return div;
  }

  function showTyping() {
    const div = document.createElement('div');
    div.className = 'chat-msg bot';
    div.id = 'typing-indicator';
    div.innerHTML = `
      <div class="msg-avatar">🤖</div>
      <div class="msg-bubble" style="padding:0;background:transparent;">
        <div class="typing-indicator">
          <div class="typing-dot"></div>
          <div class="typing-dot"></div>
          <div class="typing-dot"></div>
        </div>
      </div>`;
    messagesEl.appendChild(div);
    scrollToBottom();
  }

  function hideTyping() {
    const el = document.getElementById('typing-indicator');
    if (el) el.remove();
  }

  // ── Gọi Backend (proxy đến Gemini) ───────────────────────────
  async function callBackend(userMessage) {
    // Gửi lịch sử hội thoại rút gọn (chỉ role + text) lên backend
    const historyPayload = chatHistory.map(turn => ({
      role: turn.role,
      text: turn.text
    }));

    const res = await fetch(AI_CHAT_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        message: userMessage,
        history: historyPayload
      })
    });

    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err?.message || `HTTP ${res.status}`);
    }

    const data = await res.json();
    const reply = data?.data?.reply || 'Xin lỗi, tôi không thể trả lời ngay lúc này.';

    // Lưu vào lịch sử local (format đơn giản để gửi lại lần sau)
    chatHistory.push({ role: 'user', text: userMessage });
    chatHistory.push({ role: 'model', text: reply });

    // TỐI ƯU 1: Giữ tối đa 5 lượt chat (10 phần tử). Giảm từ 40 xuống 10.
    // Càng gửi nhiều lịch sử, số lượng token đầu vào (Input Tokens) càng lớn -> Rất nhanh hết Quota
    if (chatHistory.length > 10) chatHistory = chatHistory.slice(-10);

    // Lưu history vào SessionStorage
    sessionStorage.setItem(SESSION_KEY_HISTORY, JSON.stringify(chatHistory));

    return reply;
  }

  // ── Xử lý gửi tin ─────────────────────────────────────────
  async function sendMessage(text) {
    text = (text || inputEl.value).trim();
    if (!text || isTyping) return;

    // TỐI ƯU 2: Lọc các câu hỏi quá ngắn (VD: "a", "hi") để tránh tốn request gọi API vô ích
    if (text.length < 5) {
      addMessage('Xin lỗi, câu hỏi quá ngắn. Vui lòng nhập chi tiết hơn để tôi tư vấn chính xác nhé!', 'bot');
      inputEl.value = '';
      return;
    }

    inputEl.value = '';
    inputEl.style.height = 'auto';
    addMessage(text, 'user');
    isTyping = true;
    sendBtn.disabled = true;
    showTyping();

    try {
      const reply = await callBackend(text);
      hideTyping();
      addMessage(reply, 'bot');
    } catch (err) {
      hideTyping();
      console.error('[BookAI]', err);
      addMessage(`😔 Có lỗi xảy ra: ${err.message}. Vui lòng thử lại sau nhé!`, 'bot');
    } finally {
      isTyping = false;
      sendBtn.disabled = false;
      inputEl.focus();
    }
  }

  // ── Events ─────────────────────────────────────────────────
  toggleBtn.addEventListener('click', () => isOpen ? closeChat() : openChat());
  closeBtn.addEventListener('click', closeChat);

  sendBtn.addEventListener('click', () => sendMessage());

  inputEl.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  });

  // Auto resize textarea
  inputEl.addEventListener('input', () => {
    inputEl.style.height = 'auto';
    inputEl.style.height = Math.min(inputEl.scrollHeight, 100) + 'px';
  });

  // Quick action buttons
  document.querySelectorAll('.quick-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      if (!isOpen) openChat();
      sendMessage(btn.dataset.msg);
    });
  });

  // Đóng khi click bên ngoài
  document.addEventListener('click', (e) => {
    if (isOpen && !wrapper.contains(e.target)) closeChat();
  });

})();
