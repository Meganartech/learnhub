window.onerror = function(message, source, lineno, colno, error) {
    // Log the error to the console or send it to a server
    console.error(`Uncaught error: ${message} at <span class="math-inline">\{source\}\:</span>{lineno}:${colno}`);

    // Optionally, provide a fallback or error boundary to prevent app crashes
    // ...
};