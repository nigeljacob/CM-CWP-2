import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-config',
  imports: [FormsModule,RouterLink],
  templateUrl: './config.component.html',
  styleUrl: './config.component.css'
})
export class ConfigComponent {
  config = {
    totalTickets: '',
    releaseRate: '',
    buyingRate: '',
    maxCapacity: '',
  };

  constructor(private router: Router) {}

  onSubmit() {
    // console.log('Configuration Submitted:', this.config);
    // alert('Configuration saved successfully!');

    fetch("http://localhost:8080/api/", {
      method: "POST", // Specify the HTTP method
      headers: {
        "Content-Type": "application/json", // Specify that the request body is JSON
      },
      body: JSON.stringify({
        totalTickets: this.config.totalTickets,
        ticketReleaseRate: this.config.releaseRate,
        customerRetrievalRate: this.config.buyingRate,
        maxTicketCapacity: this.config.maxCapacity,
      }),
    })
      .then((res) => {
        if (!res.ok) {
          console.log("Failed to get a response")
        }
        return res.json();
      })
      .then((data) => {
        console.log("Response from server:", data);
      })
      .catch((error) => {
        console.error("Error occurred:", error);
      });

      alert('Configuration saved successfully!');
    

    // Redirect to Home
    this.router.navigate(['/home']);
  }
}
