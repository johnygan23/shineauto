graph TD
    Start((App Launch)) --> Login[Login Activity]
    Login -- "New User?" --> Signup[Signup Activity]
    Signup --> |Register & Select Role| Login
    Login -- "Credentials Valid?" --> CheckRole{Check Role}
    
    CheckRole -- "CUSTOMER" --> CustomerHome[Customer Activity]
    CheckRole -- "PROVIDER" --> ProviderHome[Provider Activity]
    CheckRole -- "ADMIN" --> AdminHome[Admin Activity]

    subgraph Customer Module
    CustomerHome --> C_Services[View Services]
    C_Services --> C_Book[Book Service]
    C_Book --> |Save to DB| DB[(Database)]
    CustomerHome --> C_History[View History/Upcoming]
    end

    subgraph Provider Module
    ProviderHome --> P_Pending[Pending Orders]
    P_Pending --> |Accept/Reject| DB
    ProviderHome --> P_Accepted[Accepted Orders]
    P_Accepted --> |Mark Complete| DB
    ProviderHome --> P_Manage[Manage Services]
    P_Manage --> |Add/Edit/Delete| DB
    end

    subgraph Admin Module
    AdminHome --> A_Dash[Dashboard Stats]
    AdminHome --> A_Users[Manage Users]
    A_Users --> |Delete User| DB
    AdminHome --> A_Services[Manage Services]
    A_Services --> |Delete Service| DB
    AdminHome --> A_Bookings[View All Bookings]
    end
    
    CustomerHome --> Logout
    ProviderHome --> Logout
    AdminHome --> Logout
    Logout --> Login
