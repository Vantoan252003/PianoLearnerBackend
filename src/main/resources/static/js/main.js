// Set active nav link based on current page
document.addEventListener('DOMContentLoaded', function() {
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.navbar-nav .nav-link:not(.btn-login)');
    
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        
        // Remove active class from all links
        link.classList.remove('active');
        
        // Add active class to matching link
        if (currentPath === href || 
            (currentPath === '/' && href === '/') ||
            (currentPath.startsWith(href) && href !== '/')) {
            link.classList.add('active');
        }
    });
});
