document.addEventListener("DOMContentLoaded", function() {
    const dropdown = document.querySelector('.custom-dropdown');
    const selected = dropdown.querySelector('.dropdown-selected');
    const optionsContainer = dropdown.querySelector('.dropdown-options');
    const options = dropdown.querySelectorAll('.dropdown-option');
    const hiddenInput = document.getElementById('status-dropdown');

    selected.addEventListener('click', function() {
        optionsContainer.style.display = optionsContainer.style.display === 'block' ? 'none' : 'block';
    });

    options.forEach(option => {
        option.addEventListener('click', function() {
            const value = option.getAttribute('data-value');
            const text = option.querySelector('p').textContent;
            const colorClass = option.querySelector('.status-color-circle').className.split(' ')[1];

            hiddenInput.value = value;
            selected.querySelector('p').textContent = text;
            selected.querySelector('.status-color-circle').className = `status-color-circle ${colorClass}`;
            
            optionsContainer.style.display = 'none';
        });
    });

    // Close the dropdown if clicked outside
    document.addEventListener('click', function(e) {
        if (!dropdown.contains(e.target)) {
            optionsContainer.style.display = 'none';
        }
    });
});
