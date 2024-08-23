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
            // selected.querySelector('p').textContent = text || "";
            // selected.querySelector('.status-color-circle').className = `status-color-circle ${colorClass}`;
            
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

//make them send the status
document.querySelectorAll('.dropdown-option').forEach(option => {
    option.addEventListener('click', function () {
        // Get the selected status and message
        const status = this.getAttribute('data-value');
        const indicator  =  this.getAttribute('data-class');
        const msg  =  this.getAttribute('data-message');
        // Update the displayed status
        document.getElementById('selected-status-text').textContent = msg;
        document.getElementById('active-status-color').className = indicator;
        document.getElementById('status').textContent = msg;
        document.getElementById('status-circle').className = indicator;
        // Make a POST request to update the status on the server
        fetch('/set-status', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ status: status, message: msg }),
        })
        .then(response => response.json())  // Parse JSON response
        .then(data => {
            console.log(data);
            if (data.success === "true") {
                Swal.fire({
                    icon: "success",
                    title: "Status changed",
                })
            } else {
                Swal.fire({
                    icon: "error",
                    title: "Oops...",
                    text: data.message
                })
            }
        })
        .catch((error) => console.error('Error:', error));
    }) });