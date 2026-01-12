# Contributing to KhabarLagbe

Thank you for your interest in contributing to KhabarLagbe! This document provides guidelines and instructions for contributing to the project.

## Code of Conduct

We are committed to providing a welcoming and inclusive environment. Please be respectful and considerate in all interactions.

## How to Contribute

### Reporting Bugs

1. Check if the bug has already been reported in [Issues](https://github.com/noor-87dsdp-beep/KhabarLagbe/issues)
2. If not, create a new issue with:
   - Clear, descriptive title
   - Steps to reproduce
   - Expected vs actual behavior
   - Screenshots (if applicable)
   - Device/OS information
   - App version

### Suggesting Features

1. Check existing [Issues](https://github.com/noor-87dsdp-beep/KhabarLagbe/issues) for similar suggestions
2. Create a new issue with:
   - Clear description of the feature
   - Use cases and benefits
   - Mockups or examples (if applicable)
   - Any technical considerations

### Pull Requests

1. **Fork the repository**
   ```bash
   git fork https://github.com/noor-87dsdp-beep/KhabarLagbe.git
   ```

2. **Create a branch**
   ```bash
   git checkout -b feature/your-feature-name
   # or
   git checkout -b fix/your-bug-fix
   ```

3. **Make your changes**
   - Follow the coding style (see below)
   - Write meaningful commit messages
   - Add tests if applicable
   - Update documentation

4. **Test your changes**
   ```bash
   # Android
   ./gradlew test
   ./gradlew connectedAndroidTest
   
   # Backend
   npm test
   ```

5. **Commit your changes**
   ```bash
   git add .
   git commit -m "feat: add new feature"
   # or
   git commit -m "fix: resolve issue with..."
   ```

6. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

7. **Create a Pull Request**
   - Go to the original repository
   - Click "New Pull Request"
   - Select your branch
   - Fill in the PR template
   - Link related issues

## Commit Message Guidelines

We follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

### Examples
```bash
feat(home): add restaurant search functionality

fix(cart): resolve cart total calculation issue

docs(readme): update setup instructions

style(theme): adjust color palette for better contrast

refactor(api): simplify restaurant data fetching

test(order): add unit tests for order placement

chore(deps): update dependencies to latest versions
```

## Coding Style

### Kotlin (Android)

```kotlin
// Use descriptive names
val restaurantList = restaurants.filter { it.isOpen }

// Follow Kotlin conventions
data class Restaurant(
    val id: String,
    val name: String,
    val rating: Double
)

// Use extension functions where appropriate
fun String.toTitleCase(): String {
    return this.split(" ").joinToString(" ") { 
        it.capitalize() 
    }
}

// Prefer immutability
val items = listOf(1, 2, 3) // Not mutableListOf

// Use meaningful variable names
val deliveryFeeInDollars = 5.0 // Not df or fee
```

### Compose

```kotlin
// Component naming
@Composable
fun RestaurantCard() { }

// Extract composables
@Composable
fun HomeScreen() {
    Column {
        HeaderSection()
        RestaurantList()
        BottomNavigation()
    }
}

// Use remember for state
@Composable
fun SearchBar() {
    var query by remember { mutableStateOf("") }
    TextField(
        value = query,
        onValueChange = { query = it }
    )
}
```

### Project Structure

```
feature/
├── presentation/
│   ├── FeatureScreen.kt
│   ├── FeatureViewModel.kt
│   └── components/
│       └── FeatureCard.kt
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
└── data/
    ├── repository/
    ├── remote/
    └── local/
```

## Testing Guidelines

### Unit Tests

```kotlin
@Test
fun `calculateTotal should return correct sum`() {
    // Given
    val items = listOf(
        CartItem(price = 10.0, quantity = 2),
        CartItem(price = 5.0, quantity = 3)
    )
    
    // When
    val total = cart.calculateTotal(items)
    
    // Then
    assertEquals(35.0, total, 0.001)
}
```

### UI Tests

```kotlin
@Test
fun testRestaurantCardDisplaysCorrectly() {
    composeTestRule.setContent {
        RestaurantCard(restaurant = sampleRestaurant)
    }
    
    composeTestRule
        .onNodeWithText("Pizza Paradise")
        .assertIsDisplayed()
}
```

## Documentation

- Update README.md if you add major features
- Add KDoc comments for public APIs
- Document complex logic with inline comments
- Update ARCHITECTURE.md for architectural changes

```kotlin
/**
 * Calculates the delivery fee based on distance and surge pricing.
 *
 * @param distance Distance in kilometers
 * @param isSurge Whether surge pricing is active
 * @return Delivery fee in dollars
 */
fun calculateDeliveryFee(distance: Double, isSurge: Boolean): Double {
    // Implementation
}
```

## Review Process

1. Automated checks must pass (CI/CD)
2. Code review by maintainer
3. Changes requested (if any)
4. Approval and merge

## What to Contribute

### Good First Issues
- UI improvements
- Documentation updates
- Bug fixes
- Adding tests
- Performance optimizations

### Advanced Contributions
- New features
- Architecture improvements
- API integrations
- Backend development

## Development Environment

### Required Tools
- Android Studio
- Git
- Java Development Kit (JDK) 17
- Android SDK

### Recommended Plugins
- Rainbow Brackets
- SonarLint
- Kotlin
- Material Theme UI

### Code Quality Tools
```bash
# Lint checks
./gradlew lint

# Code formatting
./gradlew ktlintFormat

# Dependency updates
./gradlew dependencyUpdates
```

## Getting Help

- Check [Documentation](README.md)
- Browse [Existing Issues](https://github.com/noor-87dsdp-beep/KhabarLagbe/issues)
- Ask in [Discussions](https://github.com/noor-87dsdp-beep/KhabarLagbe/discussions)
- Email: dev@khabarlagbe.com

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

## Recognition

Contributors will be:
- Listed in CONTRIBUTORS.md
- Mentioned in release notes
- Given credit in the app (for significant contributions)

## Thank You!

Every contribution, no matter how small, makes KhabarLagbe better. Thank you for being part of this project!

---

**Questions?** Feel free to reach out by creating an issue or discussion.
