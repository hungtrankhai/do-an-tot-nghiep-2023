package com.trankhaihung.cnpm.controller;

import com.trankhaihung.cnpm.entity.CartItem;
import com.trankhaihung.cnpm.entity.Product;
import com.trankhaihung.cnpm.service.AuthenticationService;
import com.trankhaihung.cnpm.service.ProductService;
import com.trankhaihung.cnpm.service.ShoppingCartService;
import com.trankhaihung.cnpm.util.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/management")
public class ManagementController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/product-list")
    public String getProducts(Model model) {
        boolean authenticated = authenticationService.isAuthenticated();

        if(!authenticated) {
            return "redirect:/login-page";
        }
        List<Product> products = productService.getProducts();
        model.addAttribute("products", products);
        return "product/list-product";
    }

    @GetMapping("/create-product")
    public String showCreateProductForm(Product product) {
        if(!authenticationService.isAuthenticated()) {               // kiem tra neu authenticated thi moi cho vao create product
            return "redirect:/login-page";
        }
        return "product/create-product";
    }

    @PostMapping("/submit-create-product")
    public String createProduct(@Valid Product product, @RequestParam("image") MultipartFile multipartFile, BindingResult result) throws IOException {
        if(!authenticationService.isAuthenticated()) {
            return "redirect:/login-page";
        }
        if (result.hasErrors()) {
            return "product/create-product";
        }
        String productImage = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        product.setImageName(productImage);

        String uploadDir = "product-photos/";
        FileUploadUtils.saveFile(uploadDir, productImage, multipartFile);
        productService.upsert(product);

        return "redirect:/management/product-list";
    }

    @GetMapping("/show-update-product/{id}")
    public String showUpdateProductForm(@PathVariable("id") Long id, Model model) {
        if(!authenticationService.isAuthenticated()) {
            return "redirect:/login-page";
        }
        Optional<Product> product = productService.getProductById(id);

        if (!product.isPresent()) {
            return "Invalid";
        }

        model.addAttribute("product", product.get());
        return "product/update-product";
    }

    @PostMapping("/update-product/{id}")
    public String updateProduct(@PathVariable("id") Long id, @Valid Product product, @RequestParam("image") MultipartFile multipartFile, BindingResult result) throws IOException {
        if(!authenticationService.isAuthenticated()) {
            return "redirect:/login-page";
        }
        if (result.hasErrors()) {
            return "update-product";
        }
        Optional<Product> existProduct = productService.getProductById(id);
        String imageName = existProduct.get().getImageName();

        String productImage = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        if(!StringUtils.isEmpty(productImage)) {
            product.setImageName(productImage);
            String uploadDir = "product-photos/";
            FileUploadUtils.saveFile(uploadDir, productImage, multipartFile);
            productService.upsert(product);
            return "redirect:/management/product-list";
        }
        product.setImageName(imageName);
        productService.upsert(product);
        return "redirect:/management/product-list";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        if(!authenticationService.isAuthenticated()) {
            return "redirect:/login-page";
        }
        Optional<Product> product = productService.getProductById(id);

        if (!product.isPresent()) {
            return "Invalid";
        }

        productService.deleteProduct(product.get());
        return "redirect:/management/product-list";
    }

    @GetMapping("/orders")
    public ModelAndView getOrders() {
        if(!authenticationService.isAuthenticated()) {
            return new ModelAndView("redirect:/login-page");
        }
        ModelAndView mav = new ModelAndView("product/order-list");
        List<CartItem> cartItems = shoppingCartService.getCartItemsOrderedByCustomer();
        mav.addObject("service", shoppingCartService);
        mav.addObject("cartItems", cartItems);
        return mav;
    }
}