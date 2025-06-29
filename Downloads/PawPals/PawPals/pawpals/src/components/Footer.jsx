export default function Footer() {
  return (
    <footer className="bg-base-300 text-base-content px-6 py-10 border-t border-base-200">
      <div className="max-w-6xl mx-auto grid grid-cols-1 md:grid-cols-2 gap-8 items-center text-center md:text-left">
        
        {/* Brand Info */}
        <div className="flex flex-col items-center md:items-start">
          <h2 className="text-2xl font-bold text-primary">🐾 PawPals</h2>
          <p className="mt-2 text-sm text-gray-500">
            Temukan teman berbulu baru. Adopsi hewan lebih mudah dan menyenangkan bersama PawPals! 💖
          </p>
          <p className="text-xs text-gray-400 mt-2">© 2025 Gen-Z Dev Team</p>
        </div>

        {/* Social Links */}
        <div className="flex flex-col items-center md:items-end">
          <h3 className="text-lg font-semibold mb-2">Ikuti Kami</h3>
          <div className="flex gap-4">
            <a
              href="https://instagram.com"
              target="_blank"
              rel="noopener noreferrer"
              className="btn btn-sm btn-outline"
            >
              <i className="fab fa-instagram"></i> Instagram
            </a>
            <a
              href="https://twitter.com"
              target="_blank"
              rel="noopener noreferrer"
              className="btn btn-sm btn-outline"
            >
              <i className="fab fa-twitter"></i> Twitter
            </a>
          </div>
        </div>
      </div>
    </footer>
  );
}
