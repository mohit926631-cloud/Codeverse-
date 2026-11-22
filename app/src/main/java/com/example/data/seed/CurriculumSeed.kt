package com.example.data.seed

import com.example.domain.model.Achievement
import com.example.domain.model.CodeProjectModel
import com.example.domain.model.CourseModule
import com.example.domain.model.DailyMission
import com.example.domain.model.InlineChallenge
import com.example.domain.model.LeaderboardMember
import com.example.domain.model.LearningWorld
import com.example.domain.model.Lesson
import com.example.domain.model.LessonStep
import com.example.domain.model.QuizQuestion

object CurriculumSeed {

    val worlds: List<LearningWorld> = listOf(
        // WORLD 1: Web Foundations
        LearningWorld(
            id = "world_1",
            number = 1,
            title = "Web Foundations",
            subtitle = "Internet fundamentals, HTML, CSS, Flexbox, Grid & Responsive Design",
            badgeTag = "HTML + CSS",
            accentHex = 0xFF22C5E9,
            bossName = "DR. DIV-ZERO",
            bossTitle = "Guardian of Broken Layouts",
            modules = listOf(
                buildModule(
                    id = "w1_m1",
                    worldId = "world_1",
                    title = "Internet & Semantic HTML",
                    summary = "How browsers fetch pages, HTTP basics, and semantic HTML5 structure.",
                    minutes = 8,
                    xp = 100,
                    lessons = listOf(
                        Lesson(
                            id = "w1_m1_l1",
                            moduleId = "w1_m1",
                            worldId = "world_1",
                            title = "How the Web Works & HTML5",
                            subtitle = "Clients, servers, DNS, and your first semantic page",
                            xpReward = 60,
                            gemReward = 15,
                            steps = listOf(
                                LessonStep(
                                    id = "s1",
                                    title = "Client-Server Request Cycle",
                                    explanation = "When you enter a URL, DNS translates the domain into an IP address, and your browser sends an HTTP GET request. The server responds with HTML, CSS, and JavaScript.",
                                    codeSnippet = "GET /index.html HTTP/1.1\nHost: codeverse.dev\nAccept: text/html",
                                    codeLanguage = "HTTP",
                                    byteTip = "Think of DNS as the internet's contact list and HTTP as the delivery protocol!"
                                ),
                                LessonStep(
                                    id = "s2",
                                    title = "Semantic HTML5 Tags",
                                    explanation = "Semantic elements like <header>, <main>, <article>, and <footer> give meaning to both screen readers and search engines.",
                                    codeSnippet = "<main class=\"hero\">\n  <h1>Welcome to Codeverse</h1>\n  <p>Build real full-stack apps.</p>\n  <button>Start Coding</button>\n</main>",
                                    codeLanguage = "HTML",
                                    byteTip = "Use <button> for interactive actions instead of clickable <div> elements for accessibility.",
                                    interactiveChallenge = InlineChallenge(
                                        prompt = "Which HTML tag represents the dominant content of a document?",
                                        options = listOf("<main>", "<span-body>", "<div-root>", "<head>"),
                                        correctIndex = 0,
                                        feedbackCorrect = "Spot on! <main> marks the primary landmark content.",
                                        feedbackWrong = "Close! Look for the semantic HTML5 landmark tag called <main>."
                                    )
                                )
                            ),
                            quizQuestions = listOf(
                                QuizQuestion(
                                    id = "q_w1_1",
                                    prompt = "What is the role of DNS when visiting a website?",
                                    codeContext = null,
                                    options = listOf(
                                        "Translates domain names into IP addresses",
                                        "Styles HTML elements with neon colors",
                                        "Compiles JavaScript to binary",
                                        "Stores browser cookies permanently"
                                    ),
                                    correctIndex = 0,
                                    explanation = "DNS (Domain Name System) maps human-friendly domains like codeverse.dev to numeric IP addresses."
                                ),
                                QuizQuestion(
                                    id = "q_w1_2",
                                    prompt = "Which tag creates the highest-level semantic page heading?",
                                    codeContext = "<___>Codeverse Roadmap</___>",
                                    options = listOf("<h1>", "<header>", "<title>", "<p>"),
                                    correctIndex = 0,
                                    explanation = "<h1> defines the main visible heading on a webpage."
                                )
                            )
                        )
                    )
                ),
                buildModule(
                    id = "w1_m2",
                    worldId = "world_1",
                    title = "CSS Box Model, Flexbox & Grid",
                    summary = "Master spacing, alignment, 1D Flexbox rows, and 2D CSS Grid layouts.",
                    minutes = 12,
                    xp = 130,
                    lessons = listOf(
                        Lesson(
                            id = "w1_m2_l1",
                            moduleId = "w1_m2",
                            worldId = "world_1",
                            title = "Flexbox & CSS Grid Mastery",
                            subtitle = "Responsive cards, centering, and fluid grids",
                            xpReward = 75,
                            gemReward = 20,
                            steps = listOf(
                                LessonStep(
                                    id = "s1",
                                    title = "Flexbox Alignment",
                                    explanation = "Flexbox arranges children along a main axis. Combine justify-content and align-items for effortless centering.",
                                    codeSnippet = ".nav-bar {\n  display: flex;\n  justify-content: space-between;\n  align-items: center;\n  gap: 16px;\n}",
                                    codeLanguage = "CSS",
                                    byteTip = "Use gap instead of messy child margins whenever you use Flexbox or Grid!"
                                ),
                                LessonStep(
                                    id = "s2",
                                    title = "Responsive CSS Grid",
                                    explanation = "CSS Grid controls rows and columns simultaneously. auto-fit with minmax() adapts automatically to mobile and desktop screens.",
                                    codeSnippet = ".card-grid {\n  display: grid;\n  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));\n  gap: 20px;\n}",
                                    codeLanguage = "CSS",
                                    byteTip = "1fr takes a proportional fraction of the remaining free space.",
                                    interactiveChallenge = InlineChallenge(
                                        prompt = "Which property activates a Flexbox container in CSS?",
                                        options = listOf("display: flex;", "layout: flex;", "position: flex;", "flex-box: true;"),
                                        correctIndex = 0,
                                        feedbackCorrect = "Clean CSS! display: flex turns an element into a flex container.",
                                        feedbackWrong = "Remember CSS uses the display property: display: flex;"
                                    )
                                )
                            ),
                            quizQuestions = listOf(
                                QuizQuestion(
                                    id = "q_w1_3",
                                    prompt = "Which CSS property aligns Flexbox items along the cross axis?",
                                    codeContext = ".container {\n  display: flex;\n  ___: center;\n}",
                                    options = listOf("align-items", "justify-content", "text-align", "float"),
                                    correctIndex = 0,
                                    explanation = "align-items controls cross-axis alignment, while justify-content controls the main axis."
                                ),
                                QuizQuestion(
                                    id = "q_w1_4",
                                    prompt = "What does @media (max-width: 600px) target?",
                                    codeContext = "@media (max-width: 600px) {\n  .sidebar { display: none; }\n}",
                                    options = listOf(
                                        "Viewports 600px wide or narrower",
                                        "Only desktop monitors wider than 600px",
                                        "Printers exclusively",
                                        "Elements taller than 600px"
                                    ),
                                    correctIndex = 0,
                                    explanation = "max-width applies styles when the browser viewport is at or below the specified width."
                                )
                            )
                        )
                    )
                )
            ),
            bossQuestions = listOf(
                QuizQuestion(
                    id = "boss_w1_1",
                    prompt = "DR. DIV-ZERO attacks with broken centering! How do you center a child both horizontally and vertically with Grid?",
                    codeContext = ".arena {\n  display: grid;\n  ___: center;\n}",
                    options = listOf("place-items", "center-all", "align-self", "grid-middle"),
                    correctIndex = 0,
                    explanation = "place-items: center centers items along both block and inline axes in CSS Grid.",
                    xpValue = 50
                ),
                QuizQuestion(
                    id = "boss_w1_2",
                    prompt = "Which box-sizing value includes padding and border inside the declared width?",
                    codeContext = "* { box-sizing: ___; }",
                    options = listOf("border-box", "content-box", "margin-box", "flex-box"),
                    correctIndex = 0,
                    explanation = "border-box prevents padding and borders from unexpectedly expanding element dimensions.",
                    xpValue = 50
                ),
                QuizQuestion(
                    id = "boss_w1_3",
                    prompt = "Which HTML attribute provides accessible fallback text for images?",
                    codeContext = "<img src=\"byte.png\" ___=\"BYTE mascot\" />",
                    options = listOf("alt", "title-desc", "aria-img", "caption"),
                    correctIndex = 0,
                    explanation = "The alt attribute describes the image for screen readers and displays if loading fails.",
                    xpValue = 50
                )
            )
        ),

        // WORLD 2: JavaScript
        LearningWorld(
            id = "world_2",
            number = 2,
            title = "JavaScript",
            subtitle = "Variables, Types, Functions, Arrays, Objects, DOM, Events, Async & APIs",
            badgeTag = "ES2025 JS",
            accentHex = 0xFFFFCA57,
            bossName = "NULL POINTER REX",
            bossTitle = "Phantom of Unhandled Promises",
            modules = listOf(
                buildModule(
                    id = "w2_m1",
                    worldId = "world_2",
                    title = "Variables, Logic & Functions",
                    summary = "let/const, data types, operators, loops, arrow functions, arrays & objects.",
                    minutes = 14,
                    xp = 150,
                    lessons = listOf(
                        Lesson(
                            id = "w2_m1_l1",
                            moduleId = "w2_m1",
                            worldId = "world_2",
                            title = "Modern Variables, Arrays & Arrow Functions",
                            subtitle = "Write clean ES6+ logic with const, map, and filter",
                            xpReward = 80,
                            gemReward = 20,
                            steps = listOf(
                                LessonStep(
                                    id = "s1",
                                    title = "const vs let",
                                    explanation = "Prefer const by default so references cannot be accidentally reassigned. Use let only when a variable genuinely needs reassignment.",
                                    codeSnippet = "const appName = \"Codeverse\";\nlet streak = 5;\nstreak += 1;\nconsole.log(`\${appName}: \${streak} days`);",
                                    codeLanguage = "JS",
                                    byteTip = "Template literals with backticks (`...`) let you embed expressions cleanly using \${...}!"
                                ),
                                LessonStep(
                                    id = "s2",
                                    title = "Array Transformations",
                                    explanation = "Higher-order array methods like filter() and map() transform collections without mutating the original array.",
                                    codeSnippet = "const scores = [45, 82, 96, 61];\nconst highScores = scores\n  .filter(s => s >= 80)\n  .map(s => s + 5);\n// [87, 101]",
                                    codeLanguage = "JS",
                                    byteTip = "Chaining filter() and map() keeps data pipelines readable and bug-free.",
                                    interactiveChallenge = InlineChallenge(
                                        prompt = "What does [2, 4, 6].map(x => x * 2) return?",
                                        options = listOf("[4, 8, 12]", "[2, 4, 6, 2]", "12", "[4, 16, 36]"),
                                        correctIndex = 0,
                                        feedbackCorrect = "Exact match! map() applies x * 2 to every element.",
                                        feedbackWrong = "Multiply each number in [2, 4, 6] by 2 -> [4, 8, 12]."
                                    )
                                )
                            ),
                            quizQuestions = listOf(
                                QuizQuestion(
                                    id = "q_w2_1",
                                    prompt = "What is the result of strict equality: 5 === \"5\"?",
                                    codeContext = "const isSame = (5 === \"5\");",
                                    options = listOf("false", "true", "undefined", "Throws TypeError"),
                                    correctIndex = 0,
                                    explanation = "=== checks both value and type; number 5 is not strictly equal to string \"5\"."
                                ),
                                QuizQuestion(
                                    id = "q_w2_2",
                                    prompt = "Which method attaches a click listener to a DOM button?",
                                    codeContext = "btn.___(\"click\", () => alert(\"Boost!\"));",
                                    options = listOf("addEventListener", "onEventBind", "listenClick", "attachHook"),
                                    correctIndex = 0,
                                    explanation = "element.addEventListener(event, callback) is the standard DOM event API."
                                )
                            )
                        )
                    )
                ),
                buildModule(
                    id = "w2_m2",
                    worldId = "world_2",
                    title = "DOM, Async/Await & Fetch APIs",
                    summary = "Interactive UI events, Promises, async/await, and real JSON API requests.",
                    minutes = 15,
                    xp = 170,
                    lessons = listOf(
                        Lesson(
                            id = "w2_m2_l1",
                            moduleId = "w2_m2",
                            worldId = "world_2",
                            title = "Async JavaScript & Fetch",
                            subtitle = "Consume JSON APIs safely with try/catch",
                            xpReward = 90,
                            gemReward = 25,
                            steps = listOf(
                                LessonStep(
                                    id = "s1",
                                    title = "Clean async / await",
                                    explanation = "async/await lets asynchronous network calls read like synchronous code while keeping the UI thread responsive.",
                                    codeSnippet = "async function loadProfile(user) {\n  try {\n    const res = await fetch(`https://api.example.com/users/\${user}`);\n    if (!res.ok) throw new Error(\"HTTP \" + res.status);\n    return await res.json();\n  } catch (err) {\n    console.error(\"Failed:\", err.message);\n  }\n}",
                                    codeLanguage = "JS",
                                    byteTip = "Always check res.ok! fetch() only rejects on network failures, not HTTP 404 or 500."
                                )
                            ),
                            quizQuestions = listOf(
                                QuizQuestion(
                                    id = "q_w2_3",
                                    prompt = "What does an async function always return?",
                                    codeContext = "async function getXp() { return 100; }",
                                    options = listOf("A Promise", "A synchronous number", "A JSON string", "An Event"),
                                    correctIndex = 0,
                                    explanation = "Every async function automatically wraps its return value inside a Promise."
                                )
                            )
                        )
                    )
                )
            ),
            bossQuestions = listOf(
                QuizQuestion(
                    id = "boss_w2_1",
                    prompt = "NULL POINTER REX fires a race condition! Which utility runs multiple Promises in parallel and waits for all of them?",
                    codeContext = "const [u, p] = await Promise.___([fetchUser(), fetchPosts()]);",
                    options = listOf("all", "sync", "chain", "waitEach"),
                    correctIndex = 0,
                    explanation = "Promise.all() executes concurrent promises and resolves when all succeed.",
                    xpValue = 60
                ),
                QuizQuestion(
                    id = "boss_w2_2",
                    prompt = "What does optional chaining user?.profile?.xp return if profile is undefined?",
                    codeContext = "const xp = user?.profile?.xp ?? 0;",
                    options = listOf("0 (via nullish coalescing)", "Throws TypeError", "NaN", "\"undefined\""),
                    correctIndex = 0,
                    explanation = "Optional chaining short-circuits to undefined, and ?? falls back to 0.",
                    xpValue = 60
                )
            )
        ),

        // WORLD 3: Frontend Development
        LearningWorld(
            id = "world_3",
            number = 3,
            title = "Frontend Development",
            subtitle = "React, Components, Props, State, Hooks, Routing & API Integration",
            badgeTag = "REACT",
            accentHex = 0xFF8050E8,
            bossName = "HYDRA RE-RENDER",
            bossTitle = "Lord of Infinite useEffect Loops",
            modules = listOf(
                buildModule(
                    id = "w3_m1",
                    worldId = "world_3",
                    title = "React Components, Props & State",
                    summary = "Build reusable UI components and reactive state with useState and useEffect.",
                    minutes = 16,
                    xp = 180,
                    lessons = listOf(
                        Lesson(
                            id = "w3_m1_l1",
                            moduleId = "w3_m1",
                            worldId = "world_3",
                            title = "Declarative UI & React Hooks",
                            subtitle = "useState, useEffect, props, and client-side routing",
                            xpReward = 95,
                            gemReward = 25,
                            steps = listOf(
                                LessonStep(
                                    id = "s1",
                                    title = "Components & Props",
                                    explanation = "React breaks interfaces into small, composable functions that receive read-only props and return JSX.",
                                    codeSnippet = "function StreakBadge({ days }) {\n  return (\n    <div className=\"pill\">\n      Streak: {days} days\n    </div>\n  );\n}",
                                    codeLanguage = "JSX",
                                    byteTip = "Never mutate props inside a child component — data flows downward!"
                                ),
                                LessonStep(
                                    id = "s2",
                                    title = "Managing State with useState",
                                    explanation = "When state changes through its setter, React efficiently re-renders only what changed.",
                                    codeSnippet = "const [xp, setXp] = useState(120);\nconst earnXp = () => setXp(prev => prev + 50);",
                                    codeLanguage = "JSX",
                                    byteTip = "Use the functional updater prev => prev + 50 whenever new state depends on previous state."
                                )
                            ),
                            quizQuestions = listOf(
                                QuizQuestion(
                                    id = "q_w3_1",
                                    prompt = "Why should every item in a React .map() list have a unique key prop?",
                                    codeContext = "lessons.map(item => <Card key={item.id} />)",
                                    options = listOf(
                                        "Helps React reconcile which items changed, added, or removed",
                                        "Encrypts component props in memory",
                                        "Automatically sorts the array alphabetically",
                                        "Required by CSS Flexbox"
                                    ),
                                    correctIndex = 0,
                                    explanation = "Stable keys allow React's virtual DOM diffing to preserve component state accurately."
                                )
                            )
                        )
                    )
                )
            ),
            bossQuestions = listOf(
                QuizQuestion(
                    id = "boss_w3_1",
                    prompt = "HYDRA RE-RENDER triggers an infinite loop! What dependency array makes useEffect run once on mount?",
                    codeContext = "useEffect(() => { fetchCourses(); }, ___);",
                    options = listOf("[]", "[fetchCourses()]", "null", "{ once: true }"),
                    correctIndex = 0,
                    explanation = "An empty dependency array [] tells React to run the effect only on initial mount.",
                    xpValue = 65
                )
            )
        ),

        // WORLD 4: Backend Development
        LearningWorld(
            id = "world_4",
            number = 4,
            title = "Backend Development",
            subtitle = "Node.js, Express.js, REST APIs, Authentication & Server Fundamentals",
            badgeTag = "NODE + API",
            accentHex = 0xFF38E59D,
            bossName = "GATEKEEPER 502",
            bossTitle = "Sentinel of Broken Endpoints",
            modules = listOf(
                buildModule(
                    id = "w4_m1",
                    worldId = "world_4",
                    title = "Node.js, Express & REST APIs",
                    summary = "HTTP verbs, status codes, middleware pipelines, and token authentication.",
                    minutes = 15,
                    xp = 190,
                    lessons = listOf(
                        Lesson(
                            id = "w4_m1_l1",
                            moduleId = "w4_m1",
                            worldId = "world_4",
                            title = "Building Express REST Endpoints",
                            subtitle = "Routes, JSON middleware, status codes, and JWT auth",
                            xpReward = 100,
                            gemReward = 30,
                            steps = listOf(
                                LessonStep(
                                    id = "s1",
                                    title = "Express Route Handler",
                                    explanation = "Express maps HTTP methods (GET, POST, PATCH, DELETE) to clean request/response handlers with middleware.",
                                    codeSnippet = "import express from \"express\";\nconst app = express();\napp.use(express.json());\n\napp.get(\"/api/v1/courses\", (req, res) => {\n  res.status(200).json({ status: \"ok\", total: 7 });\n});",
                                    codeLanguage = "JS",
                                    byteTip = "Return 201 Created for new POST resources and 401 Unauthorized when tokens are missing."
                                )
                            ),
                            quizQuestions = listOf(
                                QuizQuestion(
                                    id = "q_w4_1",
                                    prompt = "Which HTTP status code indicates a resource was successfully created?",
                                    codeContext = "res.status(___).json({ id: newProject.id });",
                                    options = listOf("201", "204", "403", "502"),
                                    correctIndex = 0,
                                    explanation = "201 Created signals that a POST request created a new resource on the server."
                                )
                            )
                        )
                    )
                )
            ),
            bossQuestions = listOf(
                QuizQuestion(
                    id = "boss_w4_1",
                    prompt = "GATEKEEPER 502 inspects your auth layer! How should user passwords be stored in a backend database?",
                    codeContext = "const hash = await bcrypt.hash(password, 12);",
                    options = listOf(
                        "Salted cryptographic hash (bcrypt / Argon2)",
                        "Plaintext inside a private table",
                        "Base64 encoded string",
                        "Stored in localStorage only"
                    ),
                    correctIndex = 0,
                    explanation = "Never store plaintext or reversible encodings; use slow salted password hashing like Argon2 or bcrypt.",
                    xpValue = 70
                )
            )
        ),

        // WORLD 5: Databases
        LearningWorld(
            id = "world_5",
            number = 5,
            title = "Databases",
            subtitle = "SQL, PostgreSQL, MongoDB, CRUD Operations & Relationships",
            badgeTag = "SQL + NoSQL",
            accentHex = 0xFFB89AFF,
            bossName = "PHANTOM JOIN",
            bossTitle = "Devourer of Unindexed Tables",
            modules = listOf(
                buildModule(
                    id = "w5_m1",
                    worldId = "world_5",
                    title = "PostgreSQL, MongoDB & Data Modeling",
                    summary = "Relational joins, foreign keys, document collections, and indexed queries.",
                    minutes = 16,
                    xp = 200,
                    lessons = listOf(
                        Lesson(
                            id = "w5_m1_l1",
                            moduleId = "w5_m1",
                            worldId = "world_5",
                            title = "SQL CRUD & Relational Joins",
                            subtitle = "Querying structured data safely with parameterized statements",
                            xpReward = 105,
                            gemReward = 30,
                            steps = listOf(
                                LessonStep(
                                    id = "s1",
                                    title = "JOINs & Indexes",
                                    explanation = "Relational databases like PostgreSQL enforce integrity with primary and foreign keys. Parameterized queries stop SQL injection.",
                                    codeSnippet = "SELECT u.username, COUNT(p.id) AS projects\nFROM users u\nLEFT JOIN projects p ON p.user_id = u.id\nWHERE u.id = $1\nGROUP BY u.username;",
                                    codeLanguage = "SQL",
                                    byteTip = "Always use parameterized placeholders ($1, ?) instead of concatenating user input into SQL!"
                                )
                            ),
                            quizQuestions = listOf(
                                QuizQuestion(
                                    id = "q_w5_1",
                                    prompt = "Why are parameterized queries critical in SQL?",
                                    codeContext = "db.query(\"SELECT * FROM users WHERE email = $1\", [email]);",
                                    options = listOf(
                                        "Prevents SQL injection attacks",
                                        "Converts SQL tables into MongoDB",
                                        "Removes the need for primary keys",
                                        "Compresses network packets"
                                    ),
                                    correctIndex = 0,
                                    explanation = "Parameterized queries separate SQL instructions from user-supplied data."
                                )
                            )
                        )
                    )
                )
            ),
            bossQuestions = listOf(
                QuizQuestion(
                    id = "boss_w5_1",
                    prompt = "PHANTOM JOIN slows your query to a crawl! What database structure dramatically speeds up WHERE lookups?",
                    codeContext = "CREATE ___ idx_projects_user ON projects(user_id);",
                    options = listOf("INDEX", "TRIGGER", "SCHEMA", "BUCKET"),
                    correctIndex = 0,
                    explanation = "Indexes (like B-Trees) let the database locate matching rows without scanning the whole table.",
                    xpValue = 75
                )
            )
        ),

        // WORLD 6: Professional Tools
        LearningWorld(
            id = "world_6",
            number = 6,
            title = "Professional Tools",
            subtitle = "Git, GitHub, Automated Testing, Security, Deployment & Performance",
            badgeTag = "DEVOPS",
            accentHex = 0xFFFF7A90,
            bossName = "CONFLICT TITAN",
            bossTitle = "Herald of Force-Pushed Main",
            modules = listOf(
                buildModule(
                    id = "w6_m1",
                    worldId = "world_6",
                    title = "Git Workflows, Testing & CI/CD",
                    summary = "Feature branches, pull requests, unit tests, security audits, and deployment.",
                    minutes = 14,
                    xp = 210,
                    lessons = listOf(
                        Lesson(
                            id = "w6_m1_l1",
                            moduleId = "w6_m1",
                            worldId = "world_6",
                            title = "Git Branching & Safe Releases",
                            subtitle = "Clean commits, PR reviews, and automated verification",
                            xpReward = 110,
                            gemReward = 30,
                            steps = listOf(
                                LessonStep(
                                    id = "s1",
                                    title = "Feature Branch Workflow",
                                    explanation = "Create isolated branches for each feature, run automated tests, and merge via reviewed pull requests.",
                                    codeSnippet = "git checkout -b feat/live-preview\ngit add .\ngit commit -m \"feat: add sandboxed web preview\"\ngit push -u origin feat/live-preview",
                                    codeLanguage = "BASH",
                                    byteTip = "Never commit .env secrets or API keys into Git — always keep them in .gitignore!"
                                )
                            ),
                            quizQuestions = listOf(
                                QuizQuestion(
                                    id = "q_w6_1",
                                    prompt = "What does git checkout -b feat/auth do?",
                                    codeContext = "git checkout -b feat/auth",
                                    options = listOf(
                                        "Creates a new branch feat/auth and switches to it",
                                        "Deletes the feat/auth branch permanently",
                                        "Uploads code directly to production",
                                        "Reverts the last three commits"
                                    ),
                                    correctIndex = 0,
                                    explanation = "The -b flag creates a new branch and immediately checks it out."
                                )
                            )
                        )
                    )
                )
            ),
            bossQuestions = listOf(
                QuizQuestion(
                    id = "boss_w6_1",
                    prompt = "CONFLICT TITAN tries to leak credentials! Where should production API keys live?",
                    codeContext = "const apiKey = process.env.STRIPE_SECRET;",
                    options = listOf(
                        "Server-side environment variables / secret manager",
                        "Committed inside README.md",
                        "Hardcoded in client JavaScript bundles",
                        "Stored in URL query strings"
                    ),
                    correctIndex = 0,
                    explanation = "Private secrets belong exclusively in secure environment variables / secret managers on the server.",
                    xpValue = 80
                )
            )
        ),

        // WORLD 7: Full-Stack Mastery
        LearningWorld(
            id = "world_7",
            number = 7,
            title = "Full-Stack Mastery",
            subtitle = "End-to-End Integration, Auth Systems, Architecture & Capstone Project",
            badgeTag = "CAPSTONE",
            accentHex = 0xFF22C5E9,
            bossName = "ARCHITECT PRIME",
            bossTitle = "Final Guardian of the Codeverse",
            modules = listOf(
                buildModule(
                    id = "w7_m1",
                    worldId = "world_7",
                    title = "Full-Stack Architecture & Capstone",
                    summary = "Connecting React + API + Database + Auth into a production-ready capstone.",
                    minutes = 20,
                    xp = 300,
                    lessons = listOf(
                        Lesson(
                            id = "w7_m1_l1",
                            moduleId = "w7_m1",
                            worldId = "world_7",
                            title = "Capstone System Architecture",
                            subtitle = "Designing scalable auth, data flows, and observability",
                            xpReward = 150,
                            gemReward = 50,
                            steps = listOf(
                                LessonStep(
                                    id = "s1",
                                    title = "Layered Full-Stack Flow",
                                    explanation = "A resilient full-stack app separates UI presentation, API validation, domain logic, and persistence with clear contracts and zero-trust authorization.",
                                    codeSnippet = "// Client -> Auth Token -> API Validation -> Service -> Database\nconst res = await fetch(\"/api/projects\", {\n  method: \"POST\",\n  headers: { Authorization: `Bearer \${idToken}` },\n  body: JSON.stringify({ title: \"Capstone App\" })\n});",
                                    codeLanguage = "JS",
                                    byteTip = "Validate inputs on BOTH client (fast UX) and server (real security)!"
                                )
                            ),
                            quizQuestions = listOf(
                                QuizQuestion(
                                    id = "q_w7_1",
                                    prompt = "Why must authorization checks always be enforced on the backend or database rules?",
                                    codeContext = "allow write: if request.auth.uid == userId;",
                                    options = listOf(
                                        "Client code can be modified or bypassed by attackers",
                                        "Frontend code cannot send HTTP headers",
                                        "Databases only support CSS validation",
                                        "It makes images load faster"
                                    ),
                                    correctIndex = 0,
                                    explanation = "Never trust client-only checks; enforce authentication and ownership on the server/rules layer."
                                )
                            )
                        )
                    )
                )
            ),
            bossQuestions = listOf(
                QuizQuestion(
                    id = "boss_w7_1",
                    prompt = "ARCHITECT PRIME's Final Trial: What is the safest way to roll out a breaking database schema migration with zero downtime?",
                    codeContext = "Expand -> Backfill -> Switch Reads -> Contract",
                    options = listOf(
                        "Expand-and-contract phased migration with backward compatibility",
                        "Drop the production table and recreate it live",
                        "Disable authentication during deployment",
                        "Edit binary database files manually"
                    ),
                    correctIndex = 0,
                    explanation = "Expand-and-contract keeps old and new app versions compatible throughout deployment.",
                    xpValue = 100
                )
            )
        )
    )

    private fun buildModule(
        id: String,
        worldId: String,
        title: String,
        summary: String,
        minutes: Int,
        xp: Int,
        lessons: List<Lesson>
    ) = CourseModule(
        id = id,
        worldId = worldId,
        title = title,
        summary = summary,
        estimatedMinutes = minutes,
        xpReward = xp,
        lessons = lessons
    )

    fun allLessons(): List<Lesson> = worlds.flatMap { w -> w.modules.flatMap { it.lessons } }

    fun findLesson(lessonId: String): Lesson? = allLessons().firstOrNull { it.id == lessonId }

    fun findWorld(worldId: String): LearningWorld? = worlds.firstOrNull { it.id == worldId }

    val dailyMissions: List<DailyMission> = listOf(
        DailyMission(
            id = "mission_lesson",
            title = "Complete 1 Interactive Lesson",
            subtitle = "Finish any lesson across the 7 worlds",
            xpReward = 50,
            gemReward = 15,
            targetCount = 1
        ),
        DailyMission(
            id = "mission_playground",
            title = "Run Code in Playground",
            subtitle = "Execute HTML/CSS/JS live preview once",
            xpReward = 40,
            gemReward = 10,
            targetCount = 1
        ),
        DailyMission(
            id = "mission_tutor",
            title = "Ask BYTE AI Tutor",
            subtitle = "Get an explanation or debug hint from BYTE",
            xpReward = 45,
            gemReward = 15,
            targetCount = 1
        ),
        DailyMission(
            id = "mission_boss",
            title = "Challenge a World Boss",
            subtitle = "Test your stack in Boss Battle Arena",
            xpReward = 80,
            gemReward = 30,
            targetCount = 1
        )
    )

    val achievements: List<Achievement> = listOf(
        Achievement(
            id = "ach_welcome",
            title = "Hello, Codeverse!",
            description = "Activated BYTE and joined the futuristic developer universe.",
            iconKey = "rocket",
            xpBonus = 50,
            category = "Starter"
        ),
        Achievement(
            id = "ach_first_lesson",
            title = "First Commit",
            description = "Completed your very first interactive coding lesson.",
            iconKey = "code",
            xpBonus = 100,
            category = "Learning"
        ),
        Achievement(
            id = "ach_playground_creator",
            title = "Live Builder",
            description = "Saved a custom HTML/CSS/JS project in the Coding Playground.",
            iconKey = "build",
            xpBonus = 120,
            category = "Creator"
        ),
        Achievement(
            id = "ach_boss_slayer",
            title = "Boss Slayer",
            description = "Defeated a World Boss in the Practice Arena.",
            iconKey = "shield",
            xpBonus = 200,
            category = "Combat"
        ),
        Achievement(
            id = "ach_ai_explorer",
            title = "AI Pair Programmer",
            description = "Collaborated with BYTE AI Tutor to debug or learn code.",
            iconKey = "spark",
            xpBonus = 90,
            category = "AI"
        ),
        Achievement(
            id = "ach_stylist",
            title = "BYTE Architect",
            description = "Customized BYTE's armor theme or accessory in the Studio.",
            iconKey = "palette",
            xpBonus = 75,
            category = "Customization"
        ),
        Achievement(
            id = "ach_streak_5",
            title = "Unstoppable Streak",
            description = "Maintained 500+ XP across Codeverse worlds.",
            iconKey = "flame",
            xpBonus = 180,
            category = "Mastery"
        )
    )

    val starterProjects: List<CodeProjectModel> = listOf(
        CodeProjectModel(
            id = "starter_neon_card",
            title = "Neon Profile Card",
            description = "Interactive glowing developer badge with live XP counter",
            templateTag = "HTML/CSS/JS",
            updatedAtMillis = 1763800000000L,
            htmlCode = """
<div class="card">
  <span class="badge">CODEVERSE DEV</span>
  <h1>Nova Architect</h1>
  <p>Building futuristic web apps from Android.</p>
  <div class="stats">
    <div><strong id="xp">240</strong><span>XP</span></div>
    <div><strong>7</strong><span>WORLDS</span></div>
  </div>
  <button id="boostBtn">+25 Boost XP</button>
</div>
            """.trimIndent(),
            cssCode = """
body {
  margin: 0;
  min-height: 100vh;
  display: grid;
  place-items: center;
  background: radial-gradient(circle at top, #2d2466, #141629);
  font-family: system-ui, sans-serif;
  color: #f6f7ff;
}
.card {
  width: 86%;
  max-width: 340px;
  padding: 24px;
  border-radius: 24px;
  background: rgba(37, 39, 71, 0.88);
  border: 1px solid rgba(184, 154, 255, 0.35);
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.45);
  text-align: center;
}
.badge {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 11px;
  letter-spacing: 1px;
  background: rgba(34, 197, 233, 0.16);
  color: #22C5E9;
}
h1 { margin: 14px 0 6px; font-size: 24px; }
p { color: #b5b9d6; font-size: 14px; margin-bottom: 18px; }
.stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
}
.stats strong { display: block; font-size: 22px; color: #B89AFF; }
.stats span { font-size: 11px; color: #7d82a8; }
button {
  width: 100%;
  padding: 13px;
  border: none;
  border-radius: 14px;
  font-weight: 700;
  color: white;
  background: linear-gradient(90deg, #8050E8, #22C5E9);
  cursor: pointer;
}
            """.trimIndent(),
            jsCode = """
let xp = 240;
const xpEl = document.getElementById("xp");
const btn = document.getElementById("boostBtn");

btn.addEventListener("click", () => {
  xp += 25;
  xpEl.textContent = xp;
  console.log("Boosted XP to " + xp);
});
            """.trimIndent()
        ),
        CodeProjectModel(
            id = "starter_todo_neon",
            title = "Cyber Task Board",
            description = "Minimal interactive task checklist with DOM manipulation",
            templateTag = "DOM + JS",
            updatedAtMillis = 1763700000000L,
            htmlCode = """
<div class="board">
  <h2>Mission Queue</h2>
  <div class="row">
    <input id="taskInput" placeholder="Add coding task..." />
    <button id="addBtn">Add</button>
  </div>
  <ul id="taskList">
    <li>Master CSS Flexbox</li>
    <li>Ship Express REST API</li>
  </ul>
</div>
            """.trimIndent(),
            cssCode = """
body {
  margin: 0; padding: 20px;
  background: #191B30; color: #f6f7ff;
  font-family: system-ui, sans-serif;
}
.board {
  background: #252747; padding: 20px;
  border-radius: 20px; border: 1px solid #3e4274;
}
.row { display: flex; gap: 8px; margin-bottom: 14px; }
input {
  flex: 1; padding: 10px 12px; border-radius: 10px;
  border: 1px solid #3e4274; background: #191B30; color: white;
}
button {
  padding: 10px 16px; border-radius: 10px; border: none;
  background: #8050E8; color: white; font-weight: 700;
}
ul { list-style: none; padding: 0; margin: 0; }
li {
  padding: 10px 12px; margin-top: 8px; border-radius: 10px;
  background: #1d203b; border-left: 3px solid #22C5E9;
}
            """.trimIndent(),
            jsCode = """
const input = document.getElementById("taskInput");
const list = document.getElementById("taskList");
document.getElementById("addBtn").addEventListener("click", () => {
  const val = input.value.trim();
  if (!val) return;
  const li = document.createElement("li");
  li.textContent = val;
  list.appendChild(li);
  input.value = "";
  console.log("Added mission: " + val);
});
            """.trimIndent()
        )
    )

    // Community benchmark peers displayed alongside real Firestore leaderboard entries
    val baselineLeaguePeers: List<LeaderboardMember> = listOf(
        LeaderboardMember("peer_1", "Aarav.js", "cyber_bot", 2480, 12, 19, "Quantum League"),
        LeaderboardMember("peer_2", "MayaCSS", "cyber_bot", 1940, 10, 14, "Neon League"),
        LeaderboardMember("peer_3", "RohanStack", "cyber_bot", 1420, 8, 11, "Neon League"),
        LeaderboardMember("peer_4", "ZaraNodes", "cyber_bot", 980, 6, 7, "Neon League"),
        LeaderboardMember("peer_5", "KabirSQL", "cyber_bot", 620, 4, 5, "Cyber League")
    )
}
