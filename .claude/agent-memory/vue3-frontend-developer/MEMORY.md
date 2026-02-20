# MangaOVH Frontend - Project Memory

## Project Location
- Frontend: `/Users/levanteak/Desktop/Year-2026/manga-ovh-frontend/`
- Backend API: `http://localhost:8080` (proxied via Vite `/api` prefix)

## Tech Stack
- Vue 3 + Composition API (`<script setup>`)
- Vite (port 3000)
- Vue Router 4, Pinia, Axios
- Pure CSS with CSS variables (no Tailwind)
- Dark theme, orange accent (#f97316)

## API Pattern
- All responses: `{ status, message, data }`
- Auth: JWT Bearer token stored in localStorage
- Axios interceptor auto-attaches token
- Proxy config in vite.config.js: `/api` -> `localhost:8080`

## Key Files
- API layer: `src/api/` (axios.js, auth.js, manga.js, chapters.js, bookmarks.js, history.js, ratings.js, comments.js, likes.js, user.js)
- Stores: `src/stores/auth.js`, `src/stores/manga.js`
- Router: `src/router/index.js` (lazy-loaded routes, auth guard on /profile)
- Global styles: `src/style.css` (CSS variables, utility classes)
- 8 views: Home, Catalog, MangaDetail, Reader, Login, Register, Profile, SearchResults
- 8 components: NavBar, MangaCard, MangaGrid, FilterSidebar, StarRating, BookmarkButton, ChapterList, CommentSection, Pagination

## UI Language
- Russian (ru) for all user-facing text
