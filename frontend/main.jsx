import React, { useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import {
  Activity,
  AlertTriangle,
  Banknote,
  Bell,
  CheckCircle2,
  CircleDollarSign,
  CreditCard,
  Eye,
  Landmark,
  Lock,
  LogIn,
  LogOut,
  RefreshCw,
  Send,
  ShieldCheck,
  UserPlus,
  Wallet,
} from 'lucide-react';
import './styles.css';

const DEFAULT_API_BASE = '';

function initialForm(values) {
  return values;
}

function App() {
  const [apiBase, setApiBase] = useState(localStorage.getItem('apiBase') || DEFAULT_API_BASE);
  const [auth, setAuth] = useState(() => {
    const saved = localStorage.getItem('auth');
    return saved ? JSON.parse(saved) : null;
  });
  const [busy, setBusy] = useState('');
  const [toast, setToast] = useState({ type: 'idle', message: 'Ready' });

  const [registerForm, setRegisterForm] = useState(initialForm({
    fullName: 'Jayanth Kumar',
    email: 'jayanth@example.com',
    phoneNumber: '9876543210',
    password: 'password123',
    upiPin: '123456',
  }));
  const [loginForm, setLoginForm] = useState(initialForm({
    phoneNumber: '9876543210',
    password: 'password123',
  }));
  const [accountForm, setAccountForm] = useState(initialForm({
    userId: '',
    accountNumber: '123456789012',
    bankName: 'Demo Bank',
    ifscCode: 'DEMO0123456',
    initialBalance: '5000',
  }));
  const [vpaForm, setVpaForm] = useState(initialForm({
    userId: '',
    accountId: '',
    vpa: 'jayanth@upi',
    pin: '1234',
  }));
  const [paymentForm, setPaymentForm] = useState(initialForm({
    senderVpa: 'jayanth@upi',
    receiverVpa: 'friend@upi',
    amount: '100',
    pin: '1234',
    remarks: 'Test payment',
  }));
  const [lookup, setLookup] = useState(initialForm({
    userId: '',
    accountId: '',
    vpa: 'jayanth@upi',
    transactionId: '',
  }));

  const [accounts, setAccounts] = useState([]);
  const [vpaDetails, setVpaDetails] = useState(null);
  const [paymentResult, setPaymentResult] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [notifications, setNotifications] = useState([]);
  const [fraudChecks, setFraudChecks] = useState([]);

  const effectiveUserId = auth?.userId || accountForm.userId || vpaForm.userId || lookup.userId;

  const api = useMemo(() => {
    async function request(path, options = {}) {
      const headers = {
        ...(options.body ? { 'Content-Type': 'application/json' } : {}),
        ...(auth?.token ? { Authorization: `${auth.tokenType || 'Bearer'} ${auth.token}` } : {}),
        ...(options.headers || {}),
      };

      const response = await fetch(`${apiBase}${path}`, { ...options, headers });
      const text = await response.text();
      const data = text ? safeJson(text) : null;

      if (!response.ok) {
        const message = data?.error || data?.message || text || `HTTP ${response.status}`;
        throw new Error(message);
      }
      return data;
    }
    return { request };
  }, [apiBase, auth]);

  function saveApiBase(value) {
    setApiBase(value);
    localStorage.setItem('apiBase', value);
  }

  function saveAuth(nextAuth) {
    setAuth(nextAuth);
    if (nextAuth) {
      localStorage.setItem('auth', JSON.stringify(nextAuth));
      const userId = String(nextAuth.userId || '');
      setAccountForm((form) => ({ ...form, userId }));
      setVpaForm((form) => ({ ...form, userId }));
      setLookup((form) => ({ ...form, userId }));
    } else {
      localStorage.removeItem('auth');
    }
  }

  async function run(label, action) {
    setBusy(label);
    try {
      const result = await action();
      setToast({ type: 'success', message: `${label} completed` });
      return result;
    } catch (error) {
      setToast({ type: 'error', message: error.message });
      return null;
    } finally {
      setBusy('');
    }
  }

  async function register() {
    const result = await run('Register', () => api.request('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify(registerForm),
    }));
    if (result) saveAuth(result);
  }

  async function login() {
    const result = await run('Login', () => api.request('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify(loginForm),
    }));
    if (result) saveAuth(result);
  }

  async function linkAccount() {
    const payload = {
      ...accountForm,
      userId: Number(accountForm.userId || effectiveUserId),
      initialBalance: Number(accountForm.initialBalance),
    };
    const result = await run('Link account', () => api.request('/api/accounts/link', {
      method: 'POST',
      body: JSON.stringify(payload),
    }));
    if (result) {
      setAccounts((items) => upsertById(items, result));
      setVpaForm((form) => ({
        ...form,
        userId: String(result.userId || payload.userId),
        accountId: String(result.id || form.accountId),
      }));
      setLookup((form) => ({
        ...form,
        accountId: String(result.id || form.accountId),
      }));
    }
  }

  async function loadAccounts() {
    const userId = lookup.userId || effectiveUserId;
    const result = await run('Load accounts', () => api.request(`/api/accounts/user/${userId}`));
    if (result) setAccounts(result);
  }

  async function createVpa() {
    const payload = {
      ...vpaForm,
      userId: Number(vpaForm.userId || effectiveUserId),
      accountId: Number(vpaForm.accountId),
    };
    const result = await run('Create VPA', () => api.request('/api/upi/vpa/create', {
      method: 'POST',
      body: JSON.stringify(payload),
    }));
    if (result) {
      setVpaDetails(result);
      setLookup((form) => ({ ...form, vpa: result.vpa }));
      setPaymentForm((form) => ({ ...form, senderVpa: result.vpa }));
    }
  }

  async function getVpa() {
    const result = await run('Get VPA', () => api.request(`/api/upi/vpa/${encodeURIComponent(lookup.vpa)}`));
    if (result) setVpaDetails(result);
  }

  async function sendPayment() {
    const payload = { ...paymentForm, amount: Number(paymentForm.amount) };
    const result = await run('Send payment', () => api.request('/api/upi/pay', {
      method: 'POST',
      body: JSON.stringify(payload),
    }));
    if (result) {
      setPaymentResult(result);
      setLookup((form) => ({ ...form, transactionId: result.transactionId }));
    }
  }

  async function loadTransactions() {
    const result = await run('Load transactions', () => api.request(`/api/transactions/account/${lookup.accountId}`));
    if (result) setTransactions(result);
  }

  async function getTransaction() {
    const result = await run('Get transaction', () => api.request(`/api/transactions/${lookup.transactionId}`));
    if (result) setTransactions([result]);
  }

  async function loadNotifications() {
    const result = await run('Load notifications', () => api.request(`/api/notifications/vpa/${encodeURIComponent(lookup.vpa)}`));
    if (result) setNotifications(result);
  }

  async function loadFraudChecks(kind) {
    const path = kind === 'flagged' ? '/api/fraud/flagged' : kind === 'sender'
      ? `/api/fraud/sender/${encodeURIComponent(lookup.vpa)}`
      : '/api/fraud/checks';
    const result = await run('Load fraud checks', () => api.request(path));
    if (result) setFraudChecks(result);
  }

  return (
    <main className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark"><Wallet size={24} /></div>
          <div>
            <h1>UPI Console</h1>
            <p>Payment operations</p>
          </div>
        </div>

        <label className="field compact">
          <span>API base</span>
          <input value={apiBase} onChange={(event) => saveApiBase(event.target.value)} />
        </label>

        <div className="session-panel">
          <div className="session-icon"><ShieldCheck size={20} /></div>
          <div>
            <strong>{auth?.fullName || 'Not signed in'}</strong>
            <span>{auth?.phoneNumber || 'Gateway auth pending'}</span>
          </div>
        </div>

        <button className="secondary full" onClick={() => saveAuth(null)} disabled={!auth}>
          <LogOut size={16} />
          Clear session
        </button>

        <StatusToast toast={toast} busy={busy} />
      </aside>

      <section className="workspace">
        <div className="topbar">
          <div>
            <h2>UPI Payment System</h2>
            <p>Run the core banking, UPI, transaction, notification, and fraud workflows from one place.</p>
          </div>
          <div className="status-chip">
            {busy ? <RefreshCw size={16} className="spin" /> : <CheckCircle2 size={16} />}
            {busy || 'Idle'}
          </div>
        </div>

        <div className="grid two">
          <Panel icon={<UserPlus />} title="Register">
            <FormGrid>
              <TextField label="Full name" form={registerForm} setForm={setRegisterForm} name="fullName" />
              <TextField label="Email" form={registerForm} setForm={setRegisterForm} name="email" />
              <TextField label="Phone" form={registerForm} setForm={setRegisterForm} name="phoneNumber" />
              <TextField label="Password" type="password" form={registerForm} setForm={setRegisterForm} name="password" />
              <TextField label="UPI PIN" type="password" form={registerForm} setForm={setRegisterForm} name="upiPin" />
            </FormGrid>
            <button onClick={register}><UserPlus size={16} />Register</button>
          </Panel>

          <Panel icon={<LogIn />} title="Login">
            <FormGrid>
              <TextField label="Phone" form={loginForm} setForm={setLoginForm} name="phoneNumber" />
              <TextField label="Password" type="password" form={loginForm} setForm={setLoginForm} name="password" />
            </FormGrid>
            <button onClick={login}><Lock size={16} />Login</button>
          </Panel>
        </div>

        <div className="grid two">
          <Panel icon={<Landmark />} title="Bank Accounts">
            <FormGrid>
              <TextField label="User ID" form={accountForm} setForm={setAccountForm} name="userId" placeholder={String(effectiveUserId || '')} />
              <TextField label="Account number" form={accountForm} setForm={setAccountForm} name="accountNumber" />
              <TextField label="Bank name" form={accountForm} setForm={setAccountForm} name="bankName" />
              <TextField label="IFSC" form={accountForm} setForm={setAccountForm} name="ifscCode" />
              <TextField label="Initial balance" form={accountForm} setForm={setAccountForm} name="initialBalance" />
            </FormGrid>
            <div className="actions">
              <button onClick={linkAccount}><CreditCard size={16} />Link account</button>
              <button className="secondary" onClick={loadAccounts}><Eye size={16} />Load accounts</button>
            </div>
            <DataList items={accounts} empty="No accounts loaded" fields={['id', 'bankName', 'accountNumber', 'balance', 'status']} />
          </Panel>

          <Panel icon={<Wallet />} title="VPA">
            <FormGrid>
              <TextField label="User ID" form={vpaForm} setForm={setVpaForm} name="userId" placeholder={String(effectiveUserId || '')} />
              <TextField label="Account ID" form={vpaForm} setForm={setVpaForm} name="accountId" />
              <TextField label="VPA" form={vpaForm} setForm={setVpaForm} name="vpa" />
              <TextField label="PIN" type="password" form={vpaForm} setForm={setVpaForm} name="pin" />
            </FormGrid>
            <div className="actions">
              <button onClick={createVpa}><Wallet size={16} />Create VPA</button>
              <button className="secondary" onClick={getVpa}><Eye size={16} />Lookup</button>
            </div>
            <RecordView record={vpaDetails} empty="No VPA selected" />
          </Panel>
        </div>

        <div className="grid primary">
          <Panel icon={<Send />} title="Send Payment">
            <FormGrid>
              <TextField label="Sender VPA" form={paymentForm} setForm={setPaymentForm} name="senderVpa" />
              <TextField label="Receiver VPA" form={paymentForm} setForm={setPaymentForm} name="receiverVpa" />
              <TextField label="Amount" form={paymentForm} setForm={setPaymentForm} name="amount" />
              <TextField label="PIN" type="password" form={paymentForm} setForm={setPaymentForm} name="pin" />
              <TextField label="Remarks" form={paymentForm} setForm={setPaymentForm} name="remarks" />
            </FormGrid>
            <button onClick={sendPayment}><CircleDollarSign size={16} />Initiate payment</button>
            <RecordView record={paymentResult} empty="No payment sent yet" />
          </Panel>
        </div>

        <div className="lookup-band">
          <TextField label="Lookup User ID" form={lookup} setForm={setLookup} name="userId" />
          <TextField label="Lookup Account ID" form={lookup} setForm={setLookup} name="accountId" />
          <TextField label="Lookup VPA" form={lookup} setForm={setLookup} name="vpa" />
          <TextField label="Transaction ID" form={lookup} setForm={setLookup} name="transactionId" />
        </div>

        <div className="grid three">
          <Panel icon={<Activity />} title="Transactions">
            <div className="actions">
              <button onClick={loadTransactions}><Banknote size={16} />By account</button>
              <button className="secondary" onClick={getTransaction}><Eye size={16} />By ID</button>
            </div>
            <DataList items={transactions} empty="No transactions loaded" fields={['transactionId', 'amount', 'status', 'senderVpa', 'receiverVpa']} />
          </Panel>

          <Panel icon={<Bell />} title="Notifications">
            <button onClick={loadNotifications}><Bell size={16} />Load by VPA</button>
            <DataList items={notifications} empty="No notifications loaded" fields={['type', 'message', 'recipientVpa', 'createdAt']} />
          </Panel>

          <Panel icon={<AlertTriangle />} title="Fraud Checks">
            <div className="actions">
              <button onClick={() => loadFraudChecks('all')}><Activity size={16} />All</button>
              <button className="secondary" onClick={() => loadFraudChecks('flagged')}><AlertTriangle size={16} />Flagged</button>
              <button className="secondary" onClick={() => loadFraudChecks('sender')}><Eye size={16} />Sender</button>
            </div>
            <DataList items={fraudChecks} empty="No fraud checks loaded" fields={['transactionId', 'senderVpa', 'amount', 'flagged', 'riskLevel']} />
          </Panel>
        </div>
      </section>
    </main>
  );
}

function Panel({ icon, title, children }) {
  return (
    <section className="panel">
      <header>
        <span className="panel-icon">{React.cloneElement(icon, { size: 18 })}</span>
        <h3>{title}</h3>
      </header>
      {children}
    </section>
  );
}

function FormGrid({ children }) {
  return <div className="form-grid">{children}</div>;
}

function TextField({ label, form, setForm, name, type = 'text', placeholder = '' }) {
  return (
    <label className="field">
      <span>{label}</span>
      <input
        type={type}
        value={form[name] ?? ''}
        placeholder={placeholder}
        onChange={(event) => setForm((current) => ({ ...current, [name]: event.target.value }))}
      />
    </label>
  );
}

function StatusToast({ toast, busy }) {
  return (
    <div className={`toast ${toast.type}`}>
      {busy ? <RefreshCw size={18} className="spin" /> : toast.type === 'error' ? <AlertTriangle size={18} /> : <CheckCircle2 size={18} />}
      <span>{toast.message}</span>
    </div>
  );
}

function RecordView({ record, empty }) {
  if (!record) return <p className="empty">{empty}</p>;
  return (
    <div className="record">
      {Object.entries(record).map(([key, value]) => (
        <div key={key}>
          <span>{key}</span>
          <strong>{formatValue(value)}</strong>
        </div>
      ))}
    </div>
  );
}

function DataList({ items, empty, fields }) {
  if (!items?.length) return <p className="empty">{empty}</p>;
  return (
    <div className="data-list">
      {items.map((item, index) => (
        <article key={item.id || item.transactionId || index}>
          {fields.map((field) => (
            <div key={field}>
              <span>{field}</span>
              <strong>{formatValue(item[field])}</strong>
            </div>
          ))}
        </article>
      ))}
    </div>
  );
}

function safeJson(text) {
  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

function formatValue(value) {
  if (value === null || value === undefined || value === '') return '-';
  if (typeof value === 'object') return JSON.stringify(value);
  return String(value);
}

function upsertById(items, nextItem) {
  if (!nextItem?.id) return [nextItem, ...items];
  const exists = items.some((item) => item.id === nextItem.id);
  return exists ? items.map((item) => item.id === nextItem.id ? nextItem : item) : [nextItem, ...items];
}

createRoot(document.getElementById('root')).render(<App />);
