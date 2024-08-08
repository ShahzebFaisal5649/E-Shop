// Function to load cart items
function loadCart() {
  const cart = JSON.parse(localStorage.getItem('cart')) || [];
  const cartItemsTbody = document.getElementById('cart-items');
  const cartTotalSpan = document.getElementById('cart-total');
  let total = 0;

  cartItemsTbody.innerHTML = '';

  cart.forEach((item, index) => {
    const itemTotal = item.price * item.quantity;
    total += itemTotal;
    cartItemsTbody.innerHTML += `
      <tr>
        <td>${item.name}</td>
        <td>$${item.price.toFixed(2)}</td>
        <td>
          <input type="number" value="${item.quantity}" min="1" data-index="${index}" class="quantity-input" />
        </td>
        <td>$${itemTotal.toFixed(2)}</td>
        <td><button class="btn btn-danger" onclick="removeFromCart(${index})">Remove</button></td>
      </tr>
    `;
  });

  cartTotalSpan.textContent = total.toFixed(2);

  // Add event listeners to quantity inputs
  document.querySelectorAll('.quantity-input').forEach(input => {
    input.addEventListener('change', updateQuantity);
  });
}

// Function to update quantity
function updateQuantity(event) {
  const index = event.target.dataset.index;
  const cart = JSON.parse(localStorage.getItem('cart')) || [];
  cart[index].quantity = parseInt(event.target.value, 10);
  localStorage.setItem('cart', JSON.stringify(cart));
  loadCart();
}

// Function to remove items from cart
function removeFromCart(index) {
  let cart = JSON.parse(localStorage.getItem('cart')) || [];
  cart.splice(index, 1);
  localStorage.setItem('cart', JSON.stringify(cart));
  loadCart();
}

// Function to add items to cart
function addToCart(name, price) {
  const cart = JSON.parse(localStorage.getItem('cart')) || [];
  const existingItem = cart.find(item => item.name === name);
  if (existingItem) {
    existingItem.quantity += 1;
  } else {
    cart.push({ name, price, quantity: 1 });
  }
  localStorage.setItem('cart', JSON.stringify(cart));
  loadCart();
}

// Function to checkout
function checkout() {
  alert('Proceeding to checkout.');
}

// Load cart on page load
document.addEventListener('DOMContentLoaded', loadCart);
