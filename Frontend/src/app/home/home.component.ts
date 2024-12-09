import { Component, Inject, OnDestroy, OnInit, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit, OnDestroy {
  counter: number = 0;
  interval: any;
  private socket!: WebSocket;
  isConnected: boolean = false;

  config = {
    totalTickets: 0,
    releaseRate: 0,
    buyingRate: 0,
    maxCapacity: 0,
  };

  constructor(@Inject(PLATFORM_ID) private platformId: Object) { }

  ngOnInit(): void {
    this.getConfiguration();
  }

  ngOnDestroy(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.cleanupWebSocket();
    }
  }

  startWebSocket() {
    if (this.isConnected) {
      console.log('WebSocket is already connected');
      return;
    }

    this.socket = new WebSocket('ws://localhost:8080/start');
    this.counter = 0;

    // When the WebSocket connection is opened
    this.socket.onopen = () => {
      this.isConnected = true;
      console.log('Connected to WebSocket');
    };

    // When a message is received
    this.socket.onmessage = (event) => {
      console.log('Received message:', event.data);
      const type = event.data[0]; // Assuming the first character indicates the type

      // Update counter based on message type
      if (type === '+') {
        this.counter++;
      } else if (type === '-') {
        this.counter--;
      } else {
        setTimeout(() => {
          alert(event.data);
        }, 500)
      }

      if (event.data === 'Trading complete') {
        this.socket.send('stop');
        console.log('Sent "stop" message to WebSocket server');

        // Close the WebSocket connection
        this.socket.close();
        this.isConnected = false;
        console.log('WebSocket connection closed');
      }
    };

    // When the WebSocket connection is closed
    this.socket.onclose = () => {
      this.isConnected = false;
      console.log('Disconnected from WebSocket');
    };

    // Handle any WebSocket errors
    this.socket.onerror = (error) => {
      console.error('WebSocket error:', error);
    };
  }

  getConfiguration() {
    fetch("http://localhost:8080/api/getConfiguration").then((res) => {
      if (res.ok) {
        res.json().then((data) => {
          this.config = {
            totalTickets: data.totalTickets,
            releaseRate: data.ticketReleaseRate,
            buyingRate: data.customerRetrievalRate,
            maxCapacity: data.maxTicketCapacity,
          };
          console.log("Configuration loaded:", this.config);
        });
      } else {
        alert("No configuration found on the server")
      }
    })
  }

  cleanupWebSocket() {
    if (this.socket && this.isConnected) {
      // Send the "stop" message to the backend
      this.socket.send('stop');
      console.log('Sent "stop" message to WebSocket server');

      // Close the WebSocket connection
      this.socket.close();
      this.isConnected = false;
      console.log('WebSocket connection closed');

      fetch("http://localhost:8080/api/").then((res) => {
        res.json().then((data) => {
          alert(data)
        })
      })
    }
  }
}
