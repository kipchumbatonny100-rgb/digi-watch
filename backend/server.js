/**
 * Prerequisites:
 * npm install express cors
 */
const express = require('express');
const fs = require('fs');
const path = require('path');
const cors = require('cors');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

const app = express();
const port = 3000;
const JWT_SECRET = 'secure_us_secret_key_123'; // In production, use an environment variable

const DB_FILE = path.join(__dirname, 'database.json');

app.use(cors());
app.use(express.json());

// Initialize Database File if it doesn't exist
if (!fs.existsSync(DB_FILE)) {
    const initialData = {
        users: [{ id: 1, name: "Test User", email: "test@example.com", password: "password123", address: "123 Main St", isSafe: true }],
        reports: [{ userId: 1, address: "Nairobi Central", isSafe: true, timestamp: new Date().toISOString() }]
    };
    fs.writeFileSync(DB_FILE, JSON.stringify(initialData, null, 2));
}

// Helper to read DB
function readDB() {
    try {
        const data = fs.readFileSync(DB_FILE, 'utf8');
        return JSON.parse(data);
    } catch (e) {
        return { users: [], reports: [] };
    }
}

// Helper to write DB
function writeDB(data) {
    fs.writeFileSync(DB_FILE, JSON.stringify(data, null, 2));
}

// Root endpoint for browser verification
app.get('/', (req, res) => {
    const db = readDB();
    res.send(`
        <h1>SecureUs Backend is Running!</h1>
        <p>Current Server Time: ${new Date().toLocaleString()}</p>
        <p><b>Database Stats:</b></p>
        <ul>
            <li>Users Registered: ${db.users.length}</li>
            <li>Total Reports: ${db.reports.length}</li>
        </ul>
        <p><b>API Endpoints:</b></p>
        <ul>
            <li>POST /api/login</li>
            <li>POST /api/register</li>
            <li>POST /api/report</li>

            <li>GET /api/reports</li>
        </ul>
    `);
});

// Health check endpoint
app.get('/api/ping', (req, res) => {
    res.json({ status: "ok", message: "Server is reachable" });
});

// Middleware to log all API requests
app.use('/api', (req, res, next) => {
    console.log(`[${new Date().toLocaleTimeString()}] ${req.method} ${req.url}`);
    if (Object.keys(req.body).length > 0) {
        console.log('Request Body:', JSON.stringify(req.body, null, 2));
    }
    next();
});

// Register a new user
app.post('/api/register', async (req, res) => {
    try {
        const { name, email, password, phone } = req.body;
        const db = readDB();

        if (db.users.find(u => u.email === email)) {
            console.log(`Registration failed: User ${email} already exists`);
            return res.status(400).json({ message: "User already exists", status: "error" });
        }

        // Hash password
        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(password, salt);

        const newUser = {
            id: db.users.length + 1,
            name,
            email,
            password: hashedPassword,
            phone: phone || "",
            address: "",
            isSafe: true,
            role: "user",
            themeColor: "#1976D2",
            otp: null,
            otpExpiry: null
        };
        db.users.push(newUser);
        writeDB(db);

        console.log(`New user registered: ${name} (${email})`);
        res.json({ message: "Registration successful", status: "success", user: { id: newUser.id, name: newUser.name } });
    } catch (error) {
        console.error('Registration error:', error);
        res.status(500).json({ message: "Server error during registration", status: "error" });
    }
});

// Update Settings
app.post('/api/settings/update', (req, res) => {
    const { userId, themeColor, address, phone } = req.body;
    const db = readDB();
    const user = db.users.find(u => u.id == userId);

    if (!user) return res.status(404).json({ message: "User not found", status: "error" });

    if (themeColor) user.themeColor = themeColor;
    if (address) user.address = address;
    if (phone) user.phone = phone;

    writeDB(db);
    res.json({ message: "Settings updated", status: "success", user });
});

// Change Password
app.post('/api/settings/change-password', async (req, res) => {
    const { userId, oldPassword, newPassword } = req.body;
    const db = readDB();
    const user = db.users.find(u => u.id == userId);

    if (!user) return res.status(404).json({ message: "User not found", status: "error" });

    const isMatch = await bcrypt.compare(oldPassword, user.password);
    if (!isMatch) return res.status(400).json({ message: "Incorrect old password", status: "error" });

    const salt = await bcrypt.genSalt(10);
    user.password = await bcrypt.hash(newPassword, salt);

    writeDB(db);
    res.json({ message: "Password changed successfully", status: "success" });
});

// Forgot Password - Send OTP
app.post('/api/auth/forgot-password', (req, res) => {
    const { identity, method } = req.body; // identity is email or phone
    const db = readDB();
    const user = db.users.find(u => u.email === identity || u.phone === identity);

    if (!user) return res.status(404).json({ message: "User not found", status: "error" });

    const otp = Math.floor(100000 + Math.random() * 900000).toString();
    user.otp = otp;
    user.otpExpiry = Date.now() + 600000; // 10 mins

    writeDB(db);

    console.log(`OTP for ${identity} via ${method}: ${otp}`);
    res.json({ message: `OTP sent via ${method}`, status: "success" });
});

// Verify OTP and Reset Password
app.post('/api/auth/reset-password', async (req, res) => {
    const { identity, otp, newPassword } = req.body;
    const db = readDB();
    const user = db.users.find(u => (u.email === identity || u.phone === identity) && u.otp === otp);

    if (!user || user.otpExpiry < Date.now()) {
        return res.status(400).json({ message: "Invalid or expired OTP", status: "error" });
    }

    const salt = await bcrypt.genSalt(10);
    user.password = await bcrypt.hash(newPassword, salt);
    user.otp = null;
    user.otpExpiry = null;

    writeDB(db);
    res.json({ message: "Password reset successful", status: "success" });
});

// Login
app.post('/api/login', async (req, res) => {
    try {
        const { email, password } = req.body;
        const db = readDB();
        const user = db.users.find(u => u.email === email);

        if (!user) {
            console.log(`Login failed: User not found (${email})`);
            return res.status(401).json({ message: "Invalid email or password", status: "error" });
        }

        // Check password (supports bcrypt and fallback for existing plain text during transition)
        let isMatch = false;
        if (user.password.startsWith('$2a$') || user.password.startsWith('$2b$')) {
            isMatch = await bcrypt.compare(password, user.password);
        } else {
            isMatch = (password === user.password);
        }

        if (isMatch) {
            // Create JWT Token
            const token = jwt.sign(
                { id: user.id, email: user.email, role: user.role || 'user' },
                JWT_SECRET,
                { expiresIn: '24h' }
            );

            console.log(`User logged in: ${user.name} (${email})`);
            res.json({
                message: "Login successful",
                status: "success",
                token: token,
                user: {
                    id: user.id,
                    name: user.name,
                    role: user.role || 'user',
                    themeColor: user.themeColor || "#1976D2",
                    phone: user.phone || ""
                }
            });
        } else {
            console.log(`Login failed for email: ${email}`);
            res.status(401).json({ message: "Invalid email or password", status: "error" });
        }
    } catch (error) {
        res.status(500).json({ message: "Server error during login", status: "error" });
    }
});

// Get all security reports
app.get('/api/reports', (req, res) => {
    const db = readDB();
    res.json(db.reports);
});

// Update user location and security status
app.post('/api/report', (req, res) => {
    const { userId, address, isSafe } = req.body;
    const db = readDB();

    const newReport = {
        userId,
        address,
        isSafe,
        timestamp: new Date().toISOString()
    };

    db.reports.unshift(newReport);

    const user = db.users.find(u => u.id == userId);
    if (user) {
        user.address = address;
        user.isSafe = isSafe;
    }

    writeDB(db);
    console.log(`Report added: ${address} (Safe: ${isSafe})`);
    res.json({ message: "Report added successfully", status: "success" });
});

app.listen(port, '0.0.0.0', () => {
    console.log(`SecureUs Backend running at http://0.0.0.0:${port}`);
});
